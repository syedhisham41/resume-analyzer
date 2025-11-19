package com.resumeanalyzer.auth.daoImpl;

import org.springframework.stereotype.Repository;

import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.auth.exceptions.UserNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@Repository
public class AuthDaoImpl implements AuthDAO {

	private EntityManager entityManager;

	public AuthDaoImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public User signUp(User user) {

		entityManager.persist(user);
		return user;
	}

	@Override
	public User getUserByUserName(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.userName = :name", User.class);
		query.setParameter("name", userName);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found with UserName : " + userName, e);
		}

	}

	@Override
	public User getUserByEmail(String email) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found with email : " + email, e);
		}
	}

	@Override
	public User getUserById(Integer id) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :userId", User.class);
		query.setParameter("userId", id);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found with userId : " + id, e);
		}
	}

	@Override
	public User updateUserDetails(User userDetails) {
		entityManager.persist(userDetails);
		return userDetails;
	}

	@Override
	public void deleteUser(User user) {
		entityManager.remove(user);
	}

	@Override
	public String changePassword(User user) {
		entityManager.persist(user);
		return "Password changed Successfully";
	}

}
