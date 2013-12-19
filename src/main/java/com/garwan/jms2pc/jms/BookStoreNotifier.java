package com.garwan.jms2pc.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.atomikos.jms.AtomikosConnectionFactoryBean;

@Component
public class BookStoreNotifier {

	@Autowired
	@Qualifier("atomikosConnectionFactory")
	private AtomikosConnectionFactoryBean cf;

	@Autowired
	@Qualifier("queue")
	private Destination destination;

	@Transactional
	public String sendMessage(String text) {
		Connection conn = null;
		String correlationId = System.currentTimeMillis() + "";
		try {
			conn = cf.createConnection();
			Session ses = conn.createSession(true, Session.SESSION_TRANSACTED);
			MessageProducer p = ses.createProducer(destination);
			TextMessage msg = ses.createTextMessage(text);
			msg.setJMSCorrelationID(correlationId);
			p.send(msg);
			ses.close();
			return correlationId;
		} catch (Exception e) {
			throw new RuntimeException("couldn't send a jms message", e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
			}
		}
	}

	@Transactional
	public String sendMessageWithException(String text) {
		Connection conn = null;
		String correlationId = System.currentTimeMillis() + "";
		try {
			conn = cf.createConnection();
			Session ses = conn.createSession(true, Session.SESSION_TRANSACTED);
			MessageProducer p = ses.createProducer(destination);
			TextMessage msg = ses.createTextMessage(text);
			msg.setJMSCorrelationID(correlationId);
			p.send(msg);
			// this will cause an error to occure
			throw new Exception();
		} catch (Exception e) {
			throw new RuntimeException("couldn't send a jms message", e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
			}
		}
	}
}
