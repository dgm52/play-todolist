package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Task(id: Long, label: String, usertask: String)

object Task {

   val task = {
      get[Long]("id") ~
      get[String]("label") ~
      get[String]("usertask_fk") map {
         case id~label~usertask => Task(id, label, usertask)
      }
   }

   def all(): List[Task] = DB.withConnection { implicit c =>
      SQL("select * from task where usertask_fk = 'Anonimo'").as(task *)
   }

   def getTask(id: Long): Task = DB.withConnection { implicit c =>
      SQL("select * from task where id = {id}").on(
         'id -> id
      ).as(task.single)
   }

   def create(label: String): String = DB.withConnection { implicit c =>
      SQL("insert into task (usertask_fk, label) values ('Anonimo', {label})").on(
         'label -> label
      ).executeUpdate()
      return label
   }

   def delete(id: Long): Int = {
      DB.withConnection { implicit c =>
         SQL("delete from task where id = {id}").on(
            'id -> id
            ).executeUpdate()
      }
   }

   /* Feature2 */

   def getUser(login: String): Option[String] = DB.withConnection { implicit c =>

      SQL("select login from usertask where login = {login}").on(
         'login -> login
      ).as(scalar[String].singleOpt)
   }

   def allUser(login: String): List[Task] = DB.withConnection { implicit c =>

      SQL("select * from task where usertask_fk = {usuario}").on(
         'usuario -> login
      ).as(task *)
   }

   def createUserTask(label: String, login: String): String = DB.withConnection { implicit c =>
      
      SQL("insert into task (usertask_fk, label) values ({login}, {label})").on(
         'login -> login,
         'label -> label
      ).executeUpdate()
      return label
   }
}