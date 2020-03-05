/*
 * Copyright (C) 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.claro.rbmservice.callback.messages.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;

import com.claro.rbmservice.callback.messages.lib.cards.CardOrientation;
import com.claro.rbmservice.callback.messages.lib.cards.CardWidth;
import com.claro.rbmservice.callback.messages.lib.cards.MediaHeight;

// [START of the RBM API Helper]

// [START import_libraries]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.core.ApiService.State;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.rcsbusinessmessaging.v1.RCSBusinessMessaging;
import com.google.api.services.rcsbusinessmessaging.v1.model.AgentContentMessage;
import com.google.api.services.rcsbusinessmessaging.v1.model.AgentEvent;
import com.google.api.services.rcsbusinessmessaging.v1.model.AgentMessage;
import com.google.api.services.rcsbusinessmessaging.v1.model.CardContent;
import com.google.api.services.rcsbusinessmessaging.v1.model.CarouselCard;
import com.google.api.services.rcsbusinessmessaging.v1.model.ContentInfo;
import com.google.api.services.rcsbusinessmessaging.v1.model.CreateFileRequest;
import com.google.api.services.rcsbusinessmessaging.v1.model.Media;
import com.google.api.services.rcsbusinessmessaging.v1.model.RequestCapabilityCallbackRequest;
import com.google.api.services.rcsbusinessmessaging.v1.model.RichCard;
import com.google.api.services.rcsbusinessmessaging.v1.model.StandaloneCard;
import com.google.api.services.rcsbusinessmessaging.v1.model.Suggestion;
import com.google.api.services.rcsbusinessmessaging.v1.model.Tester;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullResponse;

//[END import_libraries]
/**
 * Helper class for using the RBM API.
 */
public class RbmApiHelper {
    private static final Logger logger = Logger.getLogger(RbmApiHelper.class.getName());

    // the URL for the API endpoint
    private static final String RBM_API_URL = "https://us-rcsbusinessmessaging.googleapis.com";

    private static final String EXCEPTION_WAS_THROWN = "an exception was thrown";
    
    // credentials used for RBM agent API
    private GoogleCredential credential;

    // reference to the RBM api builder
    private RCSBusinessMessaging.Builder builder;
    
    // pubsub subscription service for our pull requests
    private Subscriber subscriber;
    
    // the name of the pub/sub pull subscription
    private static final String PUB_SUB_NAME = "rbm-agent-subscription";

    public RbmApiHelper() {
    	//String credentialsFileLocation = "C:\\rbm-agent-service-account-credentials.json";
    	String credentialsFileLocation = "rbm-agent-service-account-credentials.json";

        // initialize all libraries for sending and receiving messages
        initCredentials(credentialsFileLocation);
        initRbmApi();
        
        initPubSub(credentialsFileLocation);
    }

    /**
     * Initializes credentials used by the RBM API.
     * @param credentialsFileLocation The location for the GCP service account file.
     */
    private void initCredentials(String credentialsFileLocation) {
        logger.info("Initializing credentials for RBM.");

        try {
            //ClassLoader classLoader = getClass().getClassLoader();
        	//File file = new ClassPathResource(credentialsFileLocation).getFile();
        	InputStream iStream= new ClassPathResource(credentialsFileLocation).getInputStream();
        	
            /*this.credential = GoogleCredential.
                    fromStream(new FileInputStream(credentialsFileLocation));*/
            this.credential = GoogleCredential
                    .fromStream(iStream);
            
            

            this.credential = credential.createScoped(Arrays.asList(
                    "https://www.googleapis.com/auth/rcsbusinessmessaging"));
        } catch (Exception e) 
        {
        	e.printStackTrace();
        }
        /*catch(FileNotFoundException e) {
        }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Initializes the RBM api object.
     */
    private void initRbmApi() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // create instance of the RBM API
            builder = new RCSBusinessMessaging
                    .Builder(httpTransport, jsonFactory, null)
                    .setApplicationName(credential.getServiceAccountProjectId());

            // set the API credentials and endpoint
            builder.setHttpRequestInitializer(credential);
            builder.setRootUrl(RBM_API_URL);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes a pull subscription to receive user responses.
     */
    private void initPubSub(String credentialsFileLocation) {
        try {
            //ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(credentialsFileLocation); //File file = new File(classLoader.getResource(credentialsFileLocation).getFile());
            
            CredentialsProvider credentialsProvider =
                    FixedCredentialsProvider.create(ServiceAccountCredentials
                            .fromStream(
                                    new FileInputStream(file)
                            )
                    );

            GoogleCredential credential = GoogleCredential
                    .fromStream(new FileInputStream(file));

            String projectId = credential.getServiceAccountProjectId();
            logger.info("pojectId = " + projectId);

            ProjectSubscriptionName subscriptionName =
                    ProjectSubscriptionName.of(projectId, PUB_SUB_NAME);

            // Instantiate an asynchronous message receiver
            MessageReceiver receiver = this.getMessageReceiver();

            // create PubSub subscription
            subscriber = Subscriber.newBuilder(subscriptionName, receiver)
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            //subscriber.startAsync().awaitRunning();
            subscriber.startAsync();
            
            //subscriber.awaitTerminated();
            
        } catch(Exception e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }
    }
    
    /**
     * Creates a MessageReceiver handler for pulling new messages from
     * the pubsub subscription.
     * @return The MessageReceiver listener.
     */
    private MessageReceiver getMessageReceiver() {
        return new MessageReceiver() {
            /**
             * Handle incoming message, then ack/nack the received message.
             *
             * @param message The message sent by the user.
             * @param consumer Consumer for accepting a reply.
             */
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                String jsonResponse = message.getData().toStringUtf8();

                logger.info("RARIAS - Id : " + message.getMessageId());
                logger.info("RARIAS - " + jsonResponse);

                // use Gson to convert JSON response into a Map
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> jsonMap = gson.fromJson(jsonResponse, type);

                // make sure the map contains response text
                if(jsonMap.containsKey("text")) {
                    String userResponseText = jsonMap.get("text");
                    String senderPhoneNumber = jsonMap.get("senderPhoneNumber");
                    String messageId = jsonMap.get("messageId");

                    // let the user know we received and read the message
                    sendReadMessage(messageId, senderPhoneNumber);

                    // forward the response to our handler
                    handleUserResponse(userResponseText, senderPhoneNumber);
                }

                // let the service know we successfully processed the response
                consumer.ack();
            }
        };
    }


    /**
     * Takes the msisdn and converts it into the format we need to make API calls.
     * @param msisdn The phone number in E.164 format.
     * @return The phone number reformatted for the API.
     */
    private String convertToApiFormat(String msisdn) {
        return "phones/" + msisdn;
    }

    /**
     * Registers the device as a tester for this agent.
     * @param msisdn The phone number in E.164 format.
     */
    public void registerTester(String msisdn) throws Exception {
        Tester tester = new Tester();

        // convert the msisdn into the API format
        String clientDevice = convertToApiFormat(msisdn);

        // create the test request
        RCSBusinessMessaging.Phones.Testers.Create createTester
                = builder.build().phones().testers().create(clientDevice, tester);

        logger.info(createTester.execute().toString());
    }

    /**
     * Checks whether the device associated with the phone number is RCS enabled.
     * This uses the asynchronous capability check API.
     * @param msisdn The phone number in E.164 format.
     */
    public void performCapabilityCheck(String msisdn) throws Exception {
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        // create a random UUID for the request
        String requestId = UUID.randomUUID().toString();

        // initialize the capability request payload
        RequestCapabilityCallbackRequest capabilityCallbackRequest
                = new RequestCapabilityCallbackRequest();

        // set the request id
        capabilityCallbackRequest.setRequestId(requestId);

        logger.info("Device: " + parent);

        // build the request
        RCSBusinessMessaging.Phones.Capability.RequestCapabilityCallback request
                = builder
                .build()
                .phones()
                .capability()
                .requestCapabilityCallback(parent, capabilityCallbackRequest);

        // execute the capability request
        logger.info(request.execute().toString());
    }

    /**
     * Checks whether the device associated with the phone number is RCS enabled.
     * This uses the alpha synchronous capability check API.
     * @param msisdn The phone number in E.164 format.
     */
    public String getCapability(String msisdn) throws Exception {
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        logger.info("Device: " + parent);

        // build the request
        RCSBusinessMessaging.Phones.GetCapabilities capabilityCheck
                = builder
                .build()
                .phones()
                .getCapabilities(parent);

        capabilityCheck.setRequestId(UUID.randomUUID().toString());
        //capabilityCheck.set("messageId", UUID.randomUUID().toString());
        
        String url = capabilityCheck.buildHttpRequestUrl().toString();
        
        logger.info("Url: " + url);
        
        try {
            // execute synchronous capability check
            return capabilityCheck.execute().toString();
        } catch(GoogleJsonResponseException e) {
            e.printStackTrace();

            // return the error's description
            return e.getDetails().getMessage();
        }
    }

    /**
     * Uploads the file located at the publicly available URL to the RBM platform.
     * @param fileUrl A publicly available URL.
     * @return A unique file resource id.
     */
    public String uploadFile(String fileUrl) {
        String resourceId = null;

        logger.info("Uploading file");

        CreateFileRequest fileRequest = new CreateFileRequest();
        fileRequest.setFileUrl(fileUrl);

        try {
            RCSBusinessMessaging.Files.Create file =
                    builder.build().files().create(fileRequest);

            String jsonResponse = file.execute().toString();

            logger.info("jsonResponse:" + jsonResponse);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> jsonMap = gson.fromJson(jsonResponse, type);

            resourceId = jsonMap.get("name");
        } catch(IOException e) {
            e.printStackTrace();
        }

        return resourceId;
    }

    /**
     * Creates a card content object based on the parameters.
     * @param title The title for the card.
     * @param description The description for the card.
     * @param imageUrl The image URL for the card's media.
     * @param height The height to display the media.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public CardContent createCardContent(String title,
                                         String description,
                                         String imageUrl,
                                         MediaHeight height,
                                         List<Suggestion> suggestions) {
        CardContent cardContent = new CardContent();

        // have to build card from bottom up, starting with the media
        if(imageUrl != null) {
            // create content info for media element with the image URL
            Media media = new Media();
            media.setContentInfo(new ContentInfo().setFileUrl(imageUrl));
            media.setHeight(height.toString());

            // attach media to the card content
            cardContent.setMedia(media);
        }

        // make sure we have a title
        if(title != null) {
            cardContent.setTitle(title);
        }

        // make sure we have a description
        if(description != null) {
            cardContent.setDescription(description);
        }

        // make sure there are suggestions
        if(suggestions != null && suggestions.size() > 0) {
            cardContent.setSuggestions(suggestions);
        }

        return cardContent;
    }

    /**
     * Creates a standalone card object based on the passed in parameters.
     * @param title The title for the card.
     * @param description The description for the card.
     * @param imageUrl The image URL for the card's media.
     * @param height The height to display the media.
     * @param orientation The orientation of the card.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public StandaloneCard createStandaloneCard(String title,
                                               String description,
                                               String imageUrl,
                                               MediaHeight height,
                                               CardOrientation orientation,
                                               List<Suggestion> suggestions) {
        // create the card content representation of the parameters
        CardContent cardContent = createCardContent(
                title,
                description,
                imageUrl,
                height,
                suggestions
        );

        // create a standalone vertical card
        StandaloneCard standaloneCard = new StandaloneCard();
        standaloneCard.setCardContent(cardContent);
        standaloneCard.setCardOrientation(orientation.toString());

        return standaloneCard;
    }

    /**
     * Generic method to send a text message using the RBM api to the user with
     * the phone number msisdn.
     * @param messageText The text to send the user.
     * @param msisdn The phone number in E.164 format.
     */
    public void sendTextMessage(String messageText, String msisdn) throws IOException {
        sendTextMessage(messageText, msisdn, null);
    }

    /**
     * Generic method to send a text message using the RBM api to the user with
     * the phone number msisdn.
     * @param messageText The text to send the user.
     * @param msisdn The phone number in E.164 format.
     * @param suggestions The chip list suggestions.
     */
    public void sendTextMessage(String messageText, String msisdn, List<Suggestion> suggestions)
            throws IOException {
        // create content to send to the user
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setText(messageText);

        // attach suggestions if there are some
        if(suggestions != null && suggestions.size() > 0) {
            agentContentMessage.setSuggestions(suggestions);
        }

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending a standalone card to a client.
     * @param standaloneCard The card object to send.
     * @param msisdn The phone number in E.164 format.
     * @throws IOException
     */
    public void sendStandaloneCard(StandaloneCard standaloneCard, String msisdn) throws IOException {
        // attach the standalone card to a rich card
        RichCard richCard = new RichCard();
        richCard.setStandaloneCard(standaloneCard);

        // attach the rich card to the content for the message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of a carousel rich card to a client.
     * @param cardContents List of CardContent items to be attached to the CarourselCard.
     * @param cardWidth Width of the cards for the carousel.
     * @param msisdn The phone number in E.164 format.
     * @throws IOException
     */
    public void sendCarouselCards(List<CardContent> cardContents, CardWidth cardWidth, String msisdn)
            throws IOException {
        // create a carousel card and attach the falist of card contents
        CarouselCard carouselCard = new CarouselCard();
        carouselCard.setCardContents(cardContents);
        carouselCard.setCardWidth(cardWidth.toString());

        // attach the carousel card to a rich card
        RichCard richCard = new RichCard();
        richCard.setCarouselCard(carouselCard);

        // attach the rich card to the content for the message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of an agent message to a client.
     * @param agentMessage The message payload to send.
     * @param msisdn The phone number in E.164 format.
     */
    public void sendAgentMessage(AgentMessage agentMessage, String msisdn) throws IOException {
        // create a message request to send to the msisdn
        RCSBusinessMessaging.Phones.AgentMessages.Create message =
                builder.build().phones().agentMessages().create(convertToApiFormat(msisdn), agentMessage);

        // generate a unique message id
        message.setMessageId(UUID.randomUUID().toString());
        
        logger.info("Sending message to client " + msisdn);

        logger.info("Is alive: " + InetAddress.getByName("us-rcsbusinessmessaging.googleapis.com").isReachable(500));
        
        // execute the request, sending the text to the user's phone
        logger.info(message.execute().toString());
    }

    /**
     * Sends a READ request to a user's phone.
     * @param messageId The message id for the message that was read.
     * @param msisdn The phone number in E.164 format to send the event to.
     */
    public void sendReadMessage(String messageId, String msisdn) {
        try {
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.READ.toString());
            agentEvent.setMessageId(messageId);

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the IS_TYPING event to the user.
     * @param msisdn The phone number in E.164 format to send the event to.
     */
    public void sendIsTypingMessage(String msisdn) {
        try {
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.IS_TYPING.toString());

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Takes the user's response and creates an appropriate response.
     *
     * In this sample, the RBM agent responds with "I like USER_RESPONSE too!"
     * @param responseText The response the user sent to the agent.
     * @param senderPhoneNumber The phone number that send the response.
     */
    private void handleUserResponse(String responseText, String senderPhoneNumber) {
        responseText = responseText.toLowerCase();

        if(responseText.equals("stop")) {
            // Any real agent must support this command
            // TODO: Client typed stop, agent should no longer send messages to this msisdn
            logger.info(senderPhoneNumber + " asked to stop agent messaging");
        }
        else {
            sendIsTypingMessage(senderPhoneNumber);

            try {
                sendTextMessage("I like " + responseText + " too!",
                        senderPhoneNumber);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
// [END of the RBM API Helper]