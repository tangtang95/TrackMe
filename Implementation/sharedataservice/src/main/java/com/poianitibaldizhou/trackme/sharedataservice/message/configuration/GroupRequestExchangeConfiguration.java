package com.poianitibaldizhou.trackme.sharedataservice.message.configuration;


import com.poianitibaldizhou.trackme.sharedataservice.message.listener.GroupRequestQueueListener;
import com.poianitibaldizhou.trackme.sharedataservice.message.listener.GroupRequestQueueListenerImpl;
import com.poianitibaldizhou.trackme.sharedataservice.message.publisher.NumberOfUserInvolvedDataPublisher;
import com.poianitibaldizhou.trackme.sharedataservice.repository.FilterStatementRepository;
import com.poianitibaldizhou.trackme.sharedataservice.repository.GroupRequestRepository;
import com.poianitibaldizhou.trackme.sharedataservice.repository.UserRepository;
import com.poianitibaldizhou.trackme.sharedataservice.util.Constants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * The class configuration of group request exchange and its queues
 */
@Profile("usage-message-broker")
@Configuration
public class GroupRequestExchangeConfiguration {

    /**
     * Declare a new topic exchange regarding group requests. If it already
     * exists on the rabbitmq server, then it does nothing.
     *
     * @return the topic exchange regarding group requests
     */
    @Bean
    public TopicExchange groupRequestExchange(){
        return new TopicExchange(Constants.GROUP_REQUEST_EXCHANGE_NAME);
    }

    /**
     * Declare a new queue regarding group requests created to send it to share data service.
     * If it already exists, it does nothing
     *
     * @return the queue regarding the group request created for share data service
     */
    @Bean
    public Queue groupRequestCreatedToShareDataServiceQueue(){
        return new Queue(Constants.GROUP_REQUEST_CREATED_SHARE_DATA_QUEUE_NAME);
    }

    /**
     * Declare a new queue regarding group requests accepted to send it to share data service.
     * If it already exists, it does nothing
     *
     * @return the queue regarding the group request accepted for share data service
     */
    @Bean
    public Queue groupRequestAcceptedToShareDataServiceQueue(){
        return new Queue(Constants.GROUP_REQUEST_ACCEPTED_SHARE_DATA_QUEUE_NAME);
    }

    /**
     * Declare a new binding between groupRequestExchange and the queue of created group request for share data service.
     * If it already exists, it does nothing
     *
     * @param groupRequestExchange the bean of group request exchange
     * @param groupRequestCreatedToShareDataServiceQueue the bean of the queue of created group request
     *                                                   for share data service
     * @return the binding between groupRequestExchange and the queue of created group request for share data service
     */
    @Bean
    public Binding bindGroupRequestExchangeToCreatedShareDataQueue(TopicExchange groupRequestExchange,
                                                       Queue groupRequestCreatedToShareDataServiceQueue){
        return BindingBuilder.bind(groupRequestCreatedToShareDataServiceQueue).to(groupRequestExchange)
                .with("grouprequest.*.created");
    }

    /**
     * Declare a new binding between groupRequestExchange and the queue of accepted group request for share data service.
     * If it already exists, it does nothing
     *
     * @param groupRequestExchange the bean of group request exchange
     * @param groupRequestAcceptedToShareDataServiceQueue the bean of the queue of accepted group request
     *                                                   for share data service
     * @return the binding between groupRequestExchange and the queue of accepted group request for share data service
     */
    @Bean
    public Binding bindGroupRequestExchangeToAcceptedShareDataQueue(TopicExchange groupRequestExchange,
                                                       Queue groupRequestAcceptedToShareDataServiceQueue){
        return BindingBuilder.bind(groupRequestAcceptedToShareDataServiceQueue).to(groupRequestExchange)
                .with("grouprequest.*.accepted");
    }

    /**
     * Create the group request queue listener to receive messages regarding the group request exchange's queue
     *
     * @param userRepository the user repository
     * @param groupRequestRepository the group request repository
     * @param filterStatementRepository the filter statement repository
     * @param numberOfUserInvolvedDataPublisher the publisher of number of user involved data
     * @return the group request queue listener
     */
    @Bean
    public GroupRequestQueueListener groupRequestQueueListener(UserRepository userRepository,
                                                               GroupRequestRepository groupRequestRepository,
                                                               FilterStatementRepository filterStatementRepository,
                                                               NumberOfUserInvolvedDataPublisher numberOfUserInvolvedDataPublisher){
        return new GroupRequestQueueListenerImpl(userRepository, groupRequestRepository,
                filterStatementRepository, numberOfUserInvolvedDataPublisher);
    }

}
