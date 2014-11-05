package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import java.util.{Date}

case class Task(id: Long, label: String, usertask: String, enddate: Option[Date], category: String)

object Task {

   val task = {
      get[Long]("id") ~
      get[String]("label") ~
      get[String]("usertask_fk") ~ 
      get[Option[Date]]("enddate") ~
      get[String]("categorytask_fk") map {
         case id~label~usertask~enddate~category => Task(id, label, usertask, enddate, category)
      }
   }

   def all(): List[Task] = DB.withConnection { implicit c =>
      SQL("select * from task where usertask_fk = 'Anonimo'").as(task *)
   }

   def getTask(id: Long): Option[Task] = DB.withConnection { implicit c =>
      SQL("select * from task where id = {id}").on(
         'id -> id
      ).as(task.singleOpt)
   }

   def create(label: String): Long = createUserTaskDateCategory(label, "Anonimo", None, "Inbox")

   def delete(id: Long): Boolean = {
      DB.withConnection { implicit c =>
       val result: Int = SQL("delete from task where id = {id}").on(
         'id -> id
       ).executeUpdate()
       
       result match {
         case 1 => true
         case _ => false
       }
     }
   }

   /* Feature2 */

   def allUser(login: String): List[Task] = DB.withConnection { implicit c =>
      SQL("select * from task where usertask_fk = {usuario}").on(
         'usuario -> login
      ).as(task *)
   }

   def createUserTask(label: String, login: String): Long = DB.withConnection { implicit c =>      
      createUserTaskDateCategory(label, login, None, "Inbox")
   }

   /* !-- Feature 2 */

   /* Feature 3 */
   def allUserDate(login: String, enddate: Option[Date]): List[Task] = DB.withConnection { implicit c =>

      SQL("select * from task where usertask_fk = {usuario} and enddate = {enddate}").on(
         'usuario -> login,
         'enddate -> enddate
      ).as(task *)
   }

   def allBeforeDate(dateBefore: Option[Date]): List[Task] = DB.withConnection { implicit c =>

      SQL("select * from task where enddate < {dateBefore}").on(
         'dateBefore -> dateBefore
      ).as(task *)
   }

   def createUserTaskDate(label: String, login: String, enddate: Option[Date]): Long = DB.withConnection { implicit c =>
      createUserTaskDateCategory(label, login, enddate, "Inbox")
   }

   /* !-- Feature 3 */



   /* TDD Categorias */

   def createUserTaskDateCategory(label: String, login: String, enddate: Option[Date], category: String): Long = DB.withConnection { implicit c =>
      var date = Some(enddate)

      val id: Option[Long] = SQL("insert into task (usertask_fk, label, enddate, categorytask_fk) values ({login}, {label}, {enddate}, {category})").on(
         'login -> login,
         'label -> label,
         'enddate -> date,
         'category -> category
      ).executeInsert()

      val b: Long = id match {  
          case Some(id) => id
          case None => 0  
      }      

      id.getOrElse(-1)
   }
}