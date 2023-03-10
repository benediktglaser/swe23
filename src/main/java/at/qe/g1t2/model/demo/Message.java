package at.qe.g1t2.model.demo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import at.qe.g1t2.model.Userx;

/**
 * A class representing a message.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
public class Message implements Comparable<Message>, Serializable {

    private Userx from;
    private List<Userx> to = new LinkedList<>();
    private Date timestamp = new Date();
    private String text;

    public Message() {
        // required
    }

    public Userx getFrom() {
        return from;
    }

    public void setFrom(Userx from) {
        this.from = from;
    }

    public List<Userx> getTo() {
        return to;
    }

    public void setTo(List<Userx> to) {
        this.to = to;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
                return true;
        if (obj == null)
                return false;
        if (getClass() != obj.getClass())
                return false;
        Message other = (Message) obj;
        if (from == null) {
                if (other.from != null)
                        return false;
        } else if (!from.equals(other.from))
                return false;
        if (text == null) {
                if (other.text != null)
                        return false;
        } else if (!text.equals(other.text))
                return false;
        if (timestamp == null) {
                if (other.timestamp != null)
                        return false;
        } else if (!timestamp.equals(other.timestamp))
                return false;
        return true;
    }

    @Override
    public int compareTo(Message o) {
        return this.timestamp.compareTo(o.getTimestamp());
    }

}
