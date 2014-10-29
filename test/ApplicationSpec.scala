import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._

import play.api.test._
import play.api.test.Helpers._

import play.api.libs.json.{Json, JsValue}

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

    "send 201 on a newTask request" in {  
      running(FakeApplication()) {

        val Some(result) = route(  
          FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login","Anonimo"))
          )

        status(result) must equalTo(CREATED)
        contentType(result) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(result)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> "Anonimo")
      }      
    }

    "findTask" in {  
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

    "deleteTask" in {  
      running(FakeApplication()) {

        var idDelete = Task.create("Tarea 244")

        val Some(resultDelete) = route(  
          FakeRequest(DELETE, "/tasks/" + idDelete)
          )        

        status(resultDelete) must equalTo(OK)
      }      
    }

    // Feature 2
    "tasksUser" in {  
      running(FakeApplication()) {

        val usuario = "Dani"
        var id = Task.createUserTask("Tarea1", usuario)

        val Some(resultTasksUser) = route(FakeRequest(GET, "/" + usuario + "/tasks"))        

        status(resultTasksUser) must equalTo(OK)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")
      }      
    }

    "new userTask" in {  
      running(FakeApplication()) {

        val usuario = "Dani"

        val Some(resultTasksUser) = route(FakeRequest(POST, "/" + usuario + "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login",usuario)))

        status(resultTasksUser) must equalTo(CREATED)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> usuario)
      }      
    }

    //Feature 3

    "newtaskUserDate" in {  
      running(FakeApplication()) {

        val usuario = "Dani"
        val fecha = "2015-01-02"

        val Some(resultTasksUser) = route(FakeRequest(POST, "/" + usuario + "/" + fecha + "/tasks").withFormUrlEncodedBody(("label","Tarea 2"), ("login",usuario), ("enddate",fecha)))

        status(resultTasksUser) must equalTo(CREATED)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea 2")
        resultString must /("usertask" -> usuario)
        resultString must /("enddate" -> fecha)
      }      
    }

    "tasksUserDate" in {  
      running(FakeApplication()) {

        val formatter = new SimpleDateFormat("yyyy-MM-dd")
        val usuario = "Dani"
        val fecha = "2015-01-02"
        var date = formatter.parse(fecha)
        var dateParam = Some(date)

        Task.createUserTaskDate("Tarea1", usuario, dateParam)

        val Some(resultTasksUser) = route(FakeRequest(GET, "/" + usuario + "/" + fecha + "/tasks"))

        status(resultTasksUser) must equalTo(OK)
        contentType(resultTasksUser) must beSome.which(_ == "application/json")

        /*val resultJson: JsValue = contentAsJson(resultTasksUser)
        val resultString = Json.stringify(resultJson) 

        resultString must /("label" -> "Tarea1")
        resultString must /("usertask" -> usuario)
        resultString must /("enddate" -> fecha)*/
      }      
    }

    "tasksUserBeforeDate" in {  
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
        contentAsString(resultTasksUser) must contain ("Tarea1")
      }      
    }
  }
}