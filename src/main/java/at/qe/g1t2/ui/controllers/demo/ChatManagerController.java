package at.qe.g1t2.ui.controllers.demo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.qe.g1t2.model.Userx;
import at.qe.g1t2.model.demo.Message;
import at.qe.g1t2.repositories.UserxRepository;
import at.qe.g1t2.spring.CustomizedLogoutSuccessHandler;
import at.qe.g1t2.spring.LoginSuccessHandler;
import at.qe.g1t2.ui.websockets.WebSocketManager;
import at.qe.g1t2.utils.CDIAutowired;
import at.qe.g1t2.utils.CDIContextRelated;

/**
 * The chatManagerController is used to manage all conversations between users
 * (message-lists, i.e. chats).
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class ChatManagerController {

    @Autowired
    private UserxRepository userRepository;
    @CDIAutowired
    private WebSocketManager websocketManager;
    private Set<Userx> possibleRecipients = new ConcurrentSkipListSet<>();
    private Map<String, List<Message>> chats = new ConcurrentHashMap<>();

    /**
     * Called when a user logs in (see {@link LoginSuccessHandler}). Simply
     * initializes the chat-infrastructure for the logged in user and adds it to the
     * list of possible recipients
     *
     * @param username
     */
    public void onLogin(String username) {
        Userx user = this.userRepository.findFirstByUsername(username);
        this.possibleRecipients.add(user);
        this.chats.put(username, new LinkedList<>());
    }

    /**
     * Called when a user logs out (see {@link CustomizedLogoutSuccessHandler}).
     * Simply destroys, respectively, clears the previously initialized
     * chat-infrastructure and removes the user from the list of possible recipients
     *
     * @param username
     */
    public void onLogout(String username) {
        Userx user = this.userRepository.findFirstByUsername(username);
        this.possibleRecipients.remove(user);
        this.chats.remove(user.getUsername());
    }

    /**
     * Sends a message to specified recipients (to-property of message) using
     * websockets. Only one delivery at a time is allowed.
     *
     * @param message
     */
    public synchronized void deliver(Message message) {
        Userx sender = message.getFrom();
        List<Userx> recipients = message.getTo();
        List<String> sendTo = recipients.stream().map(Userx::getUsername).collect(Collectors.toList());
        // don't forget the sender
        sendTo.add(sender.getUsername());
        // add to chat-content
        this.addToChatContent(message, recipients);
        // also display at sender
        this.addToChatContent(message, sender);
        // notify sender and recpipient to update their chat-window
        this.websocketManager.getMessageChannel().send("msgRecieved", sendTo);
    }

    /**
     * Adds a message to the chat-content of the specified users.
     *
     * @param message The message to add
     * @param to      The recipient
     */
    private void addToChatContent(Message message, Userx to) {
        this.chats.get(to.getUsername()).add(message);
    }

    /**
     * Convenience-method. See {@link #addToChatContent(Message, Userx)}
     *
     * @param message
     * @param to
     */
    private void addToChatContent(Message message, List<Userx> to) {
        to.forEach(toUser -> this.addToChatContent(message, toUser));
    }

    public List<Message> getChatContentRef(Userx user1) {
        return Collections.unmodifiableList(this.chats.get(user1.getUsername()));
    }

    public Set<Userx> getPossibleRecipients() {
        return Collections.unmodifiableSet(possibleRecipients);
    }

}
