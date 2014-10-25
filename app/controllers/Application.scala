package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.{Date}
import java.util.Date
import java.text.SimpleDateFormat

import models.Task

case class TaskData(label: String, login: String, enddate: Option[Date])

object Application extends Controller {

   val dateWrite = Writes.dateWrites("yyyy-MM-dd")
   val formatter = new SimpleDateFormat("yyyy-MM-dd")

   val taskForm = Form(
      mapping( 
         "label" -> nonEmptyText,
         "login" -> nonEmptyText,
         "enddate" -> optional(date("yyyy-MM-dd"))
      )(TaskData.apply)(TaskData.unapply)
   )

   /* For JSON */

   implicit val taskWrites: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "label").write[String] and
      (JsPath \ "usertask").write[String] and
      (JsPath \ "enddate").writeNullable[Date](dateWrite)
   )(unlift(Task.unapply))

   /* /For JSON */

   def index = Action {
      Ok(views.html.index(Task.all(), taskForm))
   }

   def tasks = Action {
      val json = Json.toJson(Task.all())
      Ok(json)
   }

   def getTask(id: Long) = Action {
      val json = Json.toJson(Task.getTask(id))
      Ok(json)
   }

   def newTask = newTaskUser("Anonimo")

   def deleteTask(id: Long) = Action {
      Task.getTask(id) match {  
         case Some(i) => {
            val json = Json.toJson(Task.delete(id))
            Ok(json)
         }  
         case None => NotFound
      }
   }

   /* Feature 2 */

   def tasksUser(login: String) = Action {
      Task.getUser(login) match {  
          case Some(i) => {
            val json = Json.toJson(Task.allUser(i))
            Ok(json)
          }  
          case None => NotFound  
      }      
   }

   def newTaskUser(login: String) = Action { implicit request =>
     taskForm.bindFromRequest.fold(
       errors => BadRequest("Error en la peticion: form"),
       taskData => Task.getUser(login) match {
                     case Some(i) => {
                        val id: Long = Task.createUserTask(taskData.label, taskData.login)
                        val task = Task.getTask(id)
                        Created(Json.toJson(task))
                     }
                     case None => BadRequest("Error: No existe el propietario de la tarea: " + taskData.login)
     })
   }

   /* !-- Feature 2 */

   /* Feature 3 */

   def dateToOptionDate(param: Date): Option[Date] = {
      Some(param)
   }

   def newtaskUserDate(label: String, login: String, enddate: String) = Action {
      var date = formatter.parse(enddate)

      var dateParam = dateToOptionDate(date)

      Task.getUser(login) match {
         case Some(i) => {
            val json = Json.obj(
               "label" -> Json.toJson(Task.createUserTaskDate(label, i, dateParam))
            )
            Created(json)
          }  
          case None => NotFound
      }
   }

   def tasksUserDate(login: String, enddate: String) = Action {
      var formatter = new java.text.SimpleDateFormat("YYYY-MM-DD")
      var date = formatter.parse(enddate)

      var dateParam = dateToOptionDate(date)

      Task.getUser(login) match {  
          case Some(i) => {
            val json = Json.toJson(Task.allUserDate(i, dateParam))
            Ok(json)
          }  
          case None => NotFound  
      }      
   }

   def tasksUserBeforeDate(beforedate: String) = Action {
      var formatter = new java.text.SimpleDateFormat("YYYY-MM-DD")
      var date = formatter.parse(beforedate)
      var dateParam = dateToOptionDate(date)

      val json = Json.toJson(Task.allBeforeDate(dateParam))
      Ok(json)   
   }

   /* !-- Feature 3 */
}