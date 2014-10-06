package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import java.util.Date

case class Task(id: Long, label: String, usertask: String, enddate: Option[Date])

object Task {

   val task = {
      get[Long]("id") ~
      get[String]("label") ~
      get[String]("usertask_fk") ~ 
      get[Option[Date]]("enddate") map {
         //case id~label~usertask => Task(id, label, usertask)
         case id~label~usertask~enddate => Task(id, label, usertask, enddate)
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

   /* !-- Feature 2 */

   /* Feature 3 */
   def allUserDate(login: String, enddate: Option[Date]): List[Task] = DB.withConnection { implicit c =>

      SQL("select * from task where usertask_fk = {usuario} and enddate = {enddate}").on(
         'usuario -> login,
         'enddate -> enddate
      ).as(task *)
   }

   def createUserTaskDate(label: String, login: String, enddate: Option[Date]): String = DB.withConnection { implicit c =>
      var date = Some(enddate)

      SQL("insert into task (usertask_fk, label, enddate) values ({login}, {label}, {enddate})").on(
         'login -> login,
         'label -> label,
         'enddate -> date
      ).executeUpdate()
      return label
   }

   /* !-- Feature 3 */
}