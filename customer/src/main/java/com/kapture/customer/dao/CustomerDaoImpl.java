package com.kapture.customer.dao;

import com.kapture.customer.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public Customer findCustomerById(int id) {
        Session session = null;
        Transaction tx = null;
        Customer customer = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Query<Customer> query = session.createQuery("FROM Customer WHERE id = :id", Customer.class);
            query.setParameter("id", id);
            customer = query.uniqueResult();
            tx.commit();
            return customer;
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return customer;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public Optional<Customer> findCustomerByCustomerCode(String customerCode) {
        Session session = null;
        Transaction tx = null;
        Customer customer = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Query<Customer> query = session.createQuery("FROM Customer WHERE customerCode = :customerCode", Customer.class);
            query.setParameter("customerCode", customerCode);
            customer = query.uniqueResult();
            tx.commit();
            return Optional.ofNullable(customer);
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public List<Customer> getCustomersByClientIdWithPagination(int clientId, int offSet, int pageSize) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            String hql = "From Customer WHERE clientId = :clientId";
            Query<Customer> query = session.createQuery(hql, Customer.class).setFirstResult(offSet).setMaxResults(pageSize);
            query.setParameter("clientId", clientId);
            List<Customer> customerList = query.getResultList();
            tx.commit();
            return customerList;
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public Customer addCustomer(Customer customer) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(customer);
            tx.commit();
            return customer;
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public Optional<Customer> findCustomerByEmail(String email) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Query<Customer> query = session.createQuery("FROM Customer WHERE email = :email", Customer.class);
            query.setParameter("email", email);
            Customer customer = query.uniqueResult();
            tx.commit();
            return Optional.ofNullable(customer);
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public Optional<Customer> findCustomerByPhone(String phoneNo) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Query<Customer> query = session.createQuery("FROM Customer WHERE phoneNo = :phoneNo", Customer.class);
            query.setParameter("phoneNo", phoneNo);
            Customer customer = query.uniqueResult();
            tx.commit();
            return Optional.ofNullable(customer);
        } catch (Error e) {
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
