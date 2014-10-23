import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import play.api.libs.json._
import models.Task

class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in {  
      running(FakeApplication()) {  
        route(FakeRequest(GET, "/boum")) must beNone  
      }
    }

    "render the clean tasks page" in {  
      running(FakeApplication()) {

        val Some(home) = route(FakeRequest(GET, "/tasks"))

        status(home) must equalTo(OK)  
        contentType(home) must beSome.which(_ == "application/json")  
        //contentAsJson(home) must / */
      }
    }

    "newTask" in {  
      running(FakeApplication()) {

        val Some(result) = route(  
          FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label","Tarea 2"))
          )

        status(result) must equalTo(CREATED)
        contentType(result) must beSome.which(_ == "application/json")
        contentAsString(result).toInt must be_>(0)
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
        contentAsString(result) must contain ("Tarea 1")
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
  }
}