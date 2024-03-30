package com.divum.MeetingRoomBlocker.ImplementationTest.GoogleCloudApiImplementation;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

import com.divum.MeetingRoomBlocker.Service.Implementation.GoogleCloudApiImplementation.GoogleCloudApiImplementation;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GoogleCloudApiImplementationTest {


    @Mock
    private HttpTransport httpTransportMock;

    @Mock
    private JsonFactory jsonFactoryMock;

    @InjectMocks
    private GoogleCloudApiImplementation googleCloudApi;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        googleCloudApi = new GoogleCloudApiImplementation(httpTransportMock, jsonFactoryMock);
    }
    @Test
    public void createCalendar() throws Exception{

        String accessToken = "validAccessToken";
        Event event = new Event();
        event.setSummary("Meeting");
        event.setId("eventId");
        String host = "example@example.com";
        Calendar calendarMock = new Calendar(httpTransportMock,jsonFactoryMock,null);
        assertThrows(NullPointerException.class, ()->googleCloudApi.eventCreation(accessToken,event,host));


    }

    @Test
    public void testGetCalendarService() {
        String accessToken = "validAccessToken";
        GoogleCloudApiImplementation api = new GoogleCloudApiImplementation(httpTransportMock, jsonFactoryMock);
        Calendar calendar = googleCloudApi.getCalendarService(accessToken);
        assertNotNull(calendar);
        assertEquals("Eventchecking", calendar.getApplicationName());

    }
    @Test
    public void testGetCalendar(){
        String accessToken = "validAccessToken";
        String host = "example@example.com";
        String eventId="eventId";
        Calendar calendarMock = new Calendar(httpTransportMock,jsonFactoryMock,null);
        assertThrows(NullPointerException.class, ()->googleCloudApi.getEvent(accessToken,eventId,host));

    }

    @Test
    public void testUpdateCalendar(){
        String accessToken = "validAccessToken";
        String host = "example@example.com";
        String eventId="eventId";
        Event event = new Event();
        event.setSummary("Meeting");
        event.setId("eventId");
        Calendar calendarMock = new Calendar(httpTransportMock,jsonFactoryMock,null);
        assertThrows(NullPointerException.class, ()->googleCloudApi.updateEvent(accessToken,eventId,host,event));

    }
    @Test
    public void testDeleteCalendar(){
        String accessToken = "validAccessToken";
        String host = "example@example.com";
        String eventId="eventId";
        Calendar calendarMock = new Calendar(httpTransportMock,jsonFactoryMock,null);
        assertThrows(NullPointerException.class, ()->googleCloudApi.deleteEventinCalendar(accessToken,eventId,host));

    }



}

