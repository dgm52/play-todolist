package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.{Date}

import models.Task

object Application extends Controller {

   val taskForm = Form(
         "label" -> nonEmptyText//,
         //"usertask" -> nonEmptyTex
   )

   /* For JSON */

   implicit val taskWrites: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "label").write[String] and
      (JsPath \ "usertask").write[String] and
      (JsPath \ "enddate").write[Option[Date]]
   )(unlift(Task.unapply))

   /* /For JSON */

   def index = Action {
      Ok(views.html.index(Task.all(), taskForm))
      //Redirect(routes.Application.tasks)
   }

   def tasks = Action {
      val json = Json.toJson(Task.all())
      Ok(json)
   }

   def getTask(id: Long) = Action {
      val json = Json.toJson(Task.getTask(id))
      Ok(json)
   }

   def newTask = Action { implicit request =>
      taskForm.bindFromRequest.fold(
         errors => BadRequest(views.html.index(Task.all(), errors)),
         label => {
            val json = Json.obj(
               "label" -> Json.toJson(Task.create(label))
            )
            Created(json)
         }
      )
   }

   def deleteTask(id: Long) = Action {
      if(Task.delete(id) > 0)
         Redirect(routes.Application.index)
      else
         NotFound
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

   def newTaskUser(label: String, login: String) = Action {
      Task.getUser(login) match {
         case Some(i) => {
            val json = Json.obj(
               "label" -> Json.toJson(Task.createUserTask(label, i))
            )
            Created(json)
          }  
          case None => NotFound
      }
   }

   /* !-- Feature 2 */

   /* Feature 3 */

   def dateToOptionDate(param: Date): Option[Date] = {
      Some(param)
   }

   def newtaskUserDate(label: String, login: String, enddate: String) = Action {
      var formatter = new java.text.SimpleDateFormat("YYYY-MM-DD")
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