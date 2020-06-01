package org.fidoshenyata.processor;


public class ProcessorFactory{

    public static Processor processor(ProcessorEnum choice){
        switch (choice){
            default:
                return new ProcessorOkImpl();
        }
    }

    public static ProcessorEnum processorType(String choice) {
        switch (choice.toUpperCase()) {
            default:
                return ProcessorEnum.OK;
        }
    }
}
