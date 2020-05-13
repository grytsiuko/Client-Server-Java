package org.fidoshenyata;

import org.fidoshenyata.model.PacketBuilderTest;
import org.fidoshenyata.validator.PacketValidatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                PacketBuilderTest.class,
                PacketValidatorTest.class,
                PacketDecoderTest.class,
                PacketEncoderTest.class
        }
)
public class AllTestsLab1 {
}
