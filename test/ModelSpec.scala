import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models.Task

class ModelSpec extends Specification {

    def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str  
    def strToDate(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

    "Models" should {

        // Tenemos en cuenta que el usuario insertado en la BD no es Anonimo pero estamos realizando pruebas con Anonimo unicamente
        "create and delete task" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                
                Task.all() must have size(0)

                Task.create("Tarea1")
                Task.all() must have size(1)

                Task.delete(2) must equalTo(true)
                Task.all() must have size(0)
            }
        }

        "all tasks (Anonimo)" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                // Creamos tarea (con el usuario Anonimo implicito)
                Task.create("Tarea1")

                Task.all() must have size(1)
            }
        }

        "create and find task" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                Task.create("Tarea2")

                val taskTest = Task.getTask(1)
                taskTest.label must equalTo("Tarea 1000")

                val taskTest2 = Task.getTask(2)
                taskTest2.label must equalTo("Tarea2")
            }
        }

    }  
}