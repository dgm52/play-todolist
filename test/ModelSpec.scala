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

                Task.delete(1) must equalTo(true)
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

                val id = Task.create("Tarea2")

                val tarea = Some(Task.getTask(id))
                "Tarea2" must equalTo("Tarea2")
            }
        }

        // Feature 2

        "find user" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val Some(usuario) = Task.getUser("Dani")
                usuario must equalTo("Dani")
            }
        }

        "find incorrect user" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val usuario = Task.getUser("a")
                usuario must be (None)
            }
        }

        "all task for one user" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                Task.createUserTask("Tarea1", "Dani")
                Task.allUser("Dani") must have size(1)
            }
        }

    }  
}