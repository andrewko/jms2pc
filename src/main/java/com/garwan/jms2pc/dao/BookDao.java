package com.garwan.jms2pc.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.garwan.jms2pc.domain.Book;

@Repository
public class BookDao {

	@Autowired
	@Qualifier("bookSessionFactory")
	private SessionFactory sf;
	
	@Transactional(propagation = Propagation.MANDATORY)
	public Book saveBook(Book book) {
		Session session = sf.getCurrentSession();
		session.save(book);
		return book;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.MANDATORY)
	public List<Book> loadAllBooks() {
		Criteria crit = sf.getCurrentSession().createCriteria(Book.class);
		return crit.list();
	}
}
