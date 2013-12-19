package com.garwan.jms2pc;

import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.garwan.jms2pc.domain.Book;
import com.garwan.jms2pc.jms.BookStore;
import com.garwan.jms2pc.service.BookService;
import com.garwan.jms2pc.service.TransactionalService;

public class TransactionServiceTest {

	private static BrokerService broker;

	private ClassPathXmlApplicationContext ctx;
	private TransactionalService transactionalService;
	private BookService bookService;
	private BookStore bookStore;

	@BeforeClass
	public static void init() {
		try {
			broker = new BrokerService();
			broker.addConnector("tcp://localhost:61616");
			broker.setPersistent(true);
			broker.setDestinations(new ActiveMQDestination[] { new ActiveMQQueue("bookstore.queue") });
			broker.start();
		} catch (Exception e) {
			System.err.println(e);
			Assert.fail("Couldn't start the broker");
		}
	}

	@AfterClass
	public static void tearDown() {
		try {
			broker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void initTest() {
		ctx = new ClassPathXmlApplicationContext(new String[] { "datasource.xml", "transactionContext.xml", "jmsContext.xml" });
		transactionalService = ctx.getBean(TransactionalService.class);
		bookService = ctx.getBean(BookService.class);
		bookStore = ctx.getBean(BookStore.class);
	}

	@After
	public void tearDownTest() {
		AtomikosConnectionFactoryBean cf = ctx.getBean(AtomikosConnectionFactoryBean.class);
		cf.close();
		ctx.close();
	}

	@Test
	public void doTest() {
		transactionalService.doInTransaction();

		List<Book> books = bookService.loadAllBooks();
		Assert.assertEquals("Book has not been created", 1, books.size());

		String text = bookStore.receiveMessage();
		Assert.assertNotNull("Message has not been received", text);
	}

	@Test
	public void doTest2() {
		try {
			transactionalService.doInTransactionWithException();
			Assert.fail("Exception expected");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		String text = bookStore.receiveMessage();
		Assert.assertNull("A message has been received", text);

		List<Book> books = bookService.loadAllBooks();
		Assert.assertEquals("A book has been created", 0, books.size());
	}

	@Test
	public void doTest3() {
		try {
			transactionalService.doInTransactionWithException2();
			Assert.fail("Exception expected");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		String text = bookStore.receiveMessage();
		Assert.assertNull("A message has been received", text);

		List<Book> books = bookService.loadAllBooks();
		Assert.assertEquals("A book has been created", 0, books.size());
	}

	@Test
	public void doTest4() {
		try {
			transactionalService.doWithoutTransactionWithException();
			Assert.fail("Exception expected");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		List<Book> books = bookService.loadAllBooks();
		Assert.assertEquals("Book has been created", 0, books.size());

		String text = bookStore.receiveMessage();
		Assert.assertNotNull("Message has not been received", text);
	}

	@Test
	public void doTest5() {
		try {
			transactionalService.doWithoutTransactionWithException2();
			Assert.fail("Exception expected");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		List<Book> books = bookService.loadAllBooks();
		Assert.assertEquals("Book has been created", 1, books.size());

		String text = bookStore.receiveMessage();
		Assert.assertNull("Message has not been received", text);
	}

}
