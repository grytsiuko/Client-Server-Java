package org.fidoshenyata.processor;


public class ProcessorFactory{

    public static Processor processor(ProcessorEnum choice){
        switch (choice){
            case CORRECT:
                return new ProcessorCorrectImpl();
            default:
                return new ProcessorOkImpl();
        }
    }

    public static ProcessorEnum processorType(String choice) {
        switch (choice.toUpperCase()) {
            case "CORRECT":
                return ProcessorEnum.CORRECT;
            default:
                return ProcessorEnum.OK;
        }
    }
}
