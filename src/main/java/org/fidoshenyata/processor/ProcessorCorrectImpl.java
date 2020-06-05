package org.fidoshenyata.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fidoshenyata.db.DAO.Impl.CategoryDao;
import org.fidoshenyata.db.DAO.Impl.ProductDao;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.IllegalJSONException;
import org.fidoshenyata.exceptions.db.InternalSQLException;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;

import java.util.List;

import static org.fidoshenyata.packet.Message.*;

public class ProcessorCorrectImpl implements Processor {

    private CategoryService categoryService;
    private ProductService productService;
    private ProcessorUtils processorUtils;

    public ProcessorCorrectImpl() {
        categoryService = new CategoryService(new CategoryDao());
        productService  = new ProductService(new ProductDao());
        processorUtils = new ProcessorUtils();
    }

    @Override
    public Packet process(Packet packet) {
        Message inputMessage = packet.getUsefulMessage();
        Message answerMessage = processMessage(inputMessage);

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source(packet.getSource())
                .packetID(packet.getPacketID())
                .usefulMessage(answerMessage);
        return packetBuilder.build();
    }

    private Message processMessage(Message message) {

        int userID = message.getUserID();

        try {
            switch (message.getCommandType()) {
                case COMMAND_GET_CATEGORIES:
                    PagingInfo pagingInfo = processorUtils.extractPagingInfo(message.getMessage());
                    List<Category> categories = categoryService.getCategories(pagingInfo);
                    pagingInfo.setTotal(categoryService.getCount());
                    String response = processorUtils.generatePagingReply(categories, pagingInfo);
                    return processorUtils.buildSuccessMessage(response, userID);
            }
        } catch (IllegalJSONException e) {
            return processorUtils.buildErrorMessage("Illegal JSON", userID);
        } catch (InternalSQLException e) {
            return processorUtils.buildErrorMessage("Internal SQL error", userID);
        } catch (ServerSideJSONException e) {
            return processorUtils.buildErrorMessage("Error while creating response", userID);
        }

        return processorUtils.buildErrorMessage("No such command", userID);
    }

    public static void main(String[] args) {
        ProcessorCorrectImpl processor = new ProcessorCorrectImpl();
        Message getAllCategories = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_CATEGORIES)
                .message("{\"offset\": 0, \"limit\": 1}")
                .build();
        Message getAllCategoriesResponse = processor.processMessage(getAllCategories);

        System.out.println(getAllCategoriesResponse);
    }
}
