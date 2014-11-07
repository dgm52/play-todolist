import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._

import play.api.test._
import play.api.test.Helpers._

import play.api.libs.json.{Json, JsValue, JsArray}

import java.util.Date
import java.text.SimpleDateFormat

import models.Task
import controllers.Application

class ApplicationSpec extends Specification with JsonMatchers {

  "Application" should {

    "send 404 on a bad request" in {  
      running(FakeApplication()) {  
        route(FakeRequest(GET, "/boum")) must beNone  
      }
    }

    "render the empty tasks page" in {  
      running(FakeApplication()) {

        val Some(home) = route(FakeRequest(GET, "/tasks"))

        status(home) must equalTo(OK)  
        contentType(home) must beSome.which(_ == "application/json")
      }
    }

    "return CREATED on POST /tasks with EncodedBody" in {  
      running(FakeApplication()) {

        val Some(result) = route(  
          FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login","Anonimo"), ("category", "Inbox"))
          )

        status(result) must equalTo(CREATED)
        contentType(result) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(result)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> "Anonimo")
      }      
    }

    "return OK on GET /tasks/<id>" in {  
      running(FakeApplication()) {

        val id = Task.create("Tarea 1")

        val Some(result) = route(  
          FakeRequest(GET, "/tasks/" + id)
          )        

        status(result) must equalTo(OK)
        contentType(result) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(result)
        val resultString = Json.stringify(resultJson) 

        resultString must /("id" -> id)
        resultString must /("label" -> "Tarea 1")
        resultString must /("usertask" -> "Anonimo")
      }      
    }

    "return OK on DELETE /tasks/<id>" in {  
      running(FakeApplication()) {

        var idDelete = Task.create("Tarea 244")

        val Some(resultDelete) = route(  
          FakeRequest(DELETE, "/tasks/" + idDelete)
          )        

        status(resultDelete) must equalTo(OK)
      }      
    }

    // Feature 2
    "return OK on GET /<usuario>/tasks" in {  
      running(FakeApplication()) {

        val usuario = "Dani"
        var id = Task.createUserTask("Tarea1", usuario)

        val Some(resultTasksUser) = route(FakeRequest(GET, "/" + usuario + "/tasks"))        

        status(resultTasksUser) must equalTo(OK)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultJson match{
          case a: JsArray => a.value.length === 1
          case _ => throw new Exception("Error")
        }

        resultString must /#(0) /("label" -> "Tarea1")
        resultString must /#(0) /("usertask" -> usuario)
      }      
    }

    "return CREATED on POST /<usuario>/tasks with EncodedBody" in {  
      running(FakeApplication()) {

        val usuario = "Dani"

        val Some(resultTasksUser) = route(FakeRequest(POST, "/" + usuario + "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login",usuario), ("category", "Inbox")))

        status(resultTasksUser) must equalTo(CREATED)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> usuario)
      }      
    }

    //Feature 3

    "return CREATED on POST /<usuario>/<fecha>/tasks with EncodedBody" in {  
      running(FakeApplication()) {

        val usuario = "Dani"
        val fecha = "2015-01-02"

        val Some(resultTasksUser) = route(FakeRequest(POST, "/" + usuario + "/" + fecha + "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login",usuario), ("enddate",fecha), ("category", "Inbox")))

        status(resultTasksUser) must equalTo(CREATED)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> usuario)
        resultString must /("enddate" -> fecha)
      }      
    }

    "return OK on GET /<usuario>/<fecha>/tasks" in {  
      running(FakeApplication()) {

        val formatter = new SimpleDateFormat("yyyy-MM-dd")
        val usuario = "Dani"
        val fecha = "2015-01-02"
        var date = formatter.parse(fecha)
        var dateParam = Some(date)

        Task.createUserTaskDate("Tarea1", usuario, dateParam)
        Task.createUserTaskDate("Tarea2", usuario, dateParam)

        val Some(resultTasksUser) = route(FakeRequest(GET, "/" + usuario + "/" + fecha + "/tasks"))

        status(resultTasksUser) must equalTo(OK)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultJson match{
          case a: JsArray => a.value.length === 2
          case _ => throw new Exception("Error")
        }

        resultString must /#(0) /("label" -> "Tarea1")
        resultString must /#(0) /("usertask" -> usuario)
        resultString must /#(0) /("enddate" -> fecha)
        resultString must /#(1) /("label" -> "Tarea2")
      }      
    }

    "return OK on GET /<fecha>/before/tasks" in {  
      running(FakeApplication()) {

        val formatter = new SimpleDateFormat("yyyy-MM-dd")
        val usuario = "Dani"
        val fecha = "2015-01-02"
        var date = formatter.parse(fecha)
        var dateParam = Some(date)

        Task.createUserTaskDate("Tarea1", usuario, dateParam)

        val Some(resultTasksUser) = route(FakeRequest(GET, "/2015-01-03" + "/before/tasks"))

        status(resultTasksUser) must equalTo(OK)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultJson match{
          case a: JsArray => a.value.length === 1
          case _ => throw new Exception("Error")
        }

        resultString must /#(0) /("label" -> "Tarea1")
        resultString must /#(0) /("usertask" -> usuario)
        resultString must /#(0) /("enddate" -> fecha)
      }  
    }


    /* TDD Categories */

    "return CREATED on POST /<usuario>/tasks with {Category: Carrera} EncodedBody" in {  
      running(FakeApplication()) {

        val usuario = "Dani"

        val Some(resultTasksUser) = route(FakeRequest(POST, "/" + usuario + "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login",usuario), ("category", "Carrera")))

        status(resultTasksUser) must equalTo(CREATED)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> usuario)
        resultString must /("category" -> "Carrera")
      }      
    }
  }
}