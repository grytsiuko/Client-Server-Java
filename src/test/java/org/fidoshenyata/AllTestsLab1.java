package org.fidoshenyata;

import org.fidoshenyata.validator.PacketBytesValidatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                PacketBytesValidatorTest.class,
                PacketDecoderTest.class,
                PacketEncoderTest.class
        }
)
public class AllTestsLab1 {
}
