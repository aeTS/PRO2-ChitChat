import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prejeto {
	private String global;
	private String recipient;
	private String sender;
	private String text;
	private Date sent_at;
	
	private Prejeto() { }
	
	public Prejeto(String global, String recipient, String sender, 
			String text, Date sent_at){ 
		this.global = global;
		this.recipient = recipient;
		this.sender = sender;
		this.text = text;
		this.sent_at = sent_at;
	}
	
	@JsonProperty("global")
	public String getGlobal() {
		return global;
	}

	public void setGlobal(String global) {
		this.global = global;
	}
	
	@JsonProperty("recipient")
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	@JsonProperty("sender")
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	@JsonProperty("text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	@JsonProperty("sent_at")
	public Date getSent_at() {
		return sent_at;
	}

	public void setSent_at(Date sent_at) {
		this.sent_at = sent_at;
	}

}
