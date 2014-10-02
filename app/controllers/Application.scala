package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.functional.syntax._

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
      (JsPath \ "usertask").write[String]
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
      val json = Json.toJson(Task.allUser(login))
      Ok(json)
   }

}