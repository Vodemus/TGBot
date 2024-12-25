package org.example;

import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class DatabaseService {

    // Метод для добавления преподавателя в базу данных
    public void addTeacherToDatabase(String firstName, String lastName, String fatherName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Teacher teacher = new Teacher();
            teacher.setFirstname(firstName);
            teacher.setLastname(lastName);
            teacher.setFathername(fatherName);
            session.save(teacher);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Метод для добавления предмета в базу данных
    public void addSubjectToDatabase(Discipline discipline) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(discipline);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void addPararoomToDatabase(Pararoom pararoom) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(pararoom);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Метод для получения информации о расписании пар
    public String getParatimeInfo() {
        StringBuilder info = new StringBuilder();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Query<Paratime> query = session.createQuery("FROM Paratime", Paratime.class);
            List<Paratime> paratimes = query.list();
            transaction.commit();
            info.append("Расписание пар\n");
            for (Paratime paratime : paratimes) {
                info.append(paratime.getNumber()).append(". ");
                info.append(paratime.getStringStarttime()).append(" - ");
                info.append(paratime.getStringEndtime()).append("\n");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return info.toString();
    }

    // Метод для получения списка предметов
    public List<Discipline> getSubjects() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Discipline> subjects = null;
        try {
            transaction = session.beginTransaction();
            Query<Discipline> query = session.createQuery("FROM Discipline", Discipline.class);
            subjects = query.list();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return subjects;
    }

    public List<Paratype> getParatype() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Paratype> paratypes = null;
        try {
            transaction = session.beginTransaction();
            Query<Paratype> query = session.createQuery("FROM Paratype", Paratype.class);
            paratypes = query.list();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return paratypes;
    }
    public List<Pararoom> getPararooms() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Pararoom> pararooms = null;
        try {
            transaction = session.beginTransaction();
            Query<Pararoom> query = session.createQuery("FROM Pararoom", Pararoom.class);
            pararooms = query.list();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return pararooms;
    }

    // Метод для получения списка преподавателей
    public List<Teacher> getTeachers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Teacher> teachers = null;
        try {
            transaction = session.beginTransaction();
            Query<Teacher> query = session.createQuery("FROM Teacher", Teacher.class);
            teachers = query.list();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return teachers;
    }
}

