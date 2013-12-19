package com.garwan.jms2pc.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.atomikos.jms.AtomikosConnectionFactoryBean;

@Component
public class BookStore {

	@Autowired
	@Qualifier("atomikosConnectionFactory")
	private AtomikosConnectionFactoryBean cf;

	@Autowired
	@Qualifier("queue")
	private Destination destination;

	public String receiveMessage() {
		Connection conn = null;
		try {
			conn = cf.createConnection();
			conn.start();
			Session ses = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			javax.jms.MessageConsumer c = ses.createConsumer(destination);
			TextMessage msg = (TextMessage) c.receive(1000);
			ses.close();
			if (msg != null) {
				System.out.println(msg.getJMSCorrelationID());
				return msg.getText();
			}
			else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException("couldn't receive jms message", e);
		}
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
			}
		}
	}
}
