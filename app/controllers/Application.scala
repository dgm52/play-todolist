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

import models._

case class TaskData(label: String, login: String, enddate: Option[Date], category: String)

object Application extends Controller {

   val dateWrite = Writes.dateWrites("yyyy-MM-dd")
   val formatter = new SimpleDateFormat("yyyy-MM-dd")

   val taskForm = Form(
      mapping( 
         "label" -> nonEmptyText,
         "login" -> nonEmptyText,
         "enddate" -> optional(date("yyyy-MM-dd")),
         "category" -> nonEmptyText
      )(TaskData.apply)(TaskData.unapply)
   )

   /* For JSON */

   implicit val taskWrites: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "label").write[String] and
      (JsPath \ "usertask").write[String] and
      (JsPath \ "enddate").writeNullable[Date](dateWrite) and
      (JsPath \ "category").write[String]
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
      User.getUser(login) match {  
          case Some(i) => {
            val json = Json.toJson(Task.allUser(i))
            Ok(json)
          }  
          case None => NotFound  
      }      
   }

   def newTaskUser(login: String) = newtaskUserDate(login, "")
   /* !-- Feature 2 */


   /* Feature 3 */
   def dateToOptionDate(param: Date): Option[Date] = {
      Some(param)
   }

   def newtaskUserDate(login: String, enddate: String) = Action { implicit request =>
      var dateParam: Option[Date] = None

      if(!enddate.isEmpty()){
         var date = formatter.parse(enddate)
         dateParam = dateToOptionDate(date)
      }

      taskForm.bindFromRequest.fold(
       errors => BadRequest("Error en la peticion: form"),
       taskData => User.getUser(login) match {
                     case Some(i) => {

                        if(Category.exists(taskData.category, login)){
                           val id: Long = Task.createUserTaskDateCategory(taskData.label, login, taskData.category, dateParam)
                           val task = Task.getTask(id)
                           Created(Json.toJson(task))
                        }
                        else NotFound("NotFound: No existe la categoria " + taskData.category + " para el usuario: " + login)
                     }
                     case None => BadRequest("Error: No existe el propietario de la tarea: " + login)
     })   
   }

   def tasksUserDate(login: String, enddate: String) = Action {
      var date = formatter.parse(enddate)
      var dateParam = dateToOptionDate(date)

      User.getUser(login) match {  
          case Some(i) => {
            val json = Json.toJson(Task.allUserDate(i, dateParam))
            Ok(json)
          }  
          case None => NotFound 
      }  
   }

   def tasksUserBeforeDate(beforedate: String) = Action {
      var date = formatter.parse(beforedate)
      var dateParam = dateToOptionDate(date)

      val json = Json.toJson(Task.allBeforeDate(dateParam))
      Ok(json)   
   }

   /* !-- Feature 3 */



   /* TDD Categories */
   def all4Category4User(user: String, category: String) = Action {
      User.getUser(user) match {  
          case Some(i) => {
            if(Category.exists(category, user)){
            val json = Json.toJson(Task.all4Category4User(user, category))
            Ok(json)
            }
            else NotFound("NotFound: No existe la categoria " + category + " para el usuario: " + user)
          }  
          case None => NotFound  
      } 
   }

}