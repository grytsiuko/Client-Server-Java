package org.fidoshenyata.Lab2.Processor;


public class ProcessorFactory{

    public static Processor processor(ProcessorEnum choice){
        switch (choice){
            default:
                return new ProcessorOkImpl();
        }
    }
}
