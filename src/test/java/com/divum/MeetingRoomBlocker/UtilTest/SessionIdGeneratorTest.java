package com.divum.MeetingRoomBlocker.UtilTest;

import com.divum.MeetingRoomBlocker.Util.SessionIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class SessionIdGeneratorTest {

    @InjectMocks
    private SessionIdGenerator sessionIdGenerator;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRandomId() {
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String id1 = sessionIdGenerator.getRandomId();
        String id2 = sessionIdGenerator.getRandomId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);

        assertTrue(id1.matches("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}$"));
        assertTrue(id2.matches("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}$"));
    }
}
