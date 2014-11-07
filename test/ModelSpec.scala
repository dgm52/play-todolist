import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models._

import java.util.Date
import java.text.SimpleDateFormat

class ModelSpec extends Specification{

    def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str  
    def strToDate(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
    val formatter = new SimpleDateFormat("yyyy-MM-dd")

    "Models" should {

        "create, check that we have one task and delete a task, checking that there aren't tasks now" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                
                Task.all() must have size(0)

                Task.create("Tarea1")
                Task.all() must have size(1)

                Task.delete(1) must equalTo(true)
                Task.all() must have size(0)
            }
        }

        "create a task and check there is one task" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                // Creamos tarea (usuario Anonimo implicito)
                Task.create("Tarea1")
                Task.all() must have size(1)
            }
        }

        "create and find task" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val id = Task.create("Tarea2")

                val Some(tarea) = Task.getTask(id)
                tarea.label must equalTo("Tarea2")
            }
        }

        // Feature 2
        "find user" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val Some(usuario) = User.getUser("Dani")
                usuario must equalTo("Dani")
            }
        }

        "find incorrect user" in {  
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val usuario = User.getUser("a")
                usuario must be (None)
            }
        }

        "returns all task for one user" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                Task.createUserTask("Tarea1", "Dani")
                Task.allUser("Dani") must have size(1)
            }
        }

        //Feature 3
        "createUserTaskDate" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                var date = formatter.parse("2015-01-02")
                var dateParam = Some(date)

                Task.createUserTaskDate("Tarea1", "Dani", dateParam)
                Task.allUser("Dani") must have size(1)
            }
        }

        "allUserTaskDate: must have 1 task" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                var date = formatter.parse("2015-01-02")
                var dateParam = Some(date)

                Task.createUserTaskDate("Tarea1", "Dani", dateParam)
                Task.allUserDate("Dani", dateParam) must have size(1)
            }
        }

        "allBeforeDate en una fecha concreta: must have 0 task" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                var date = formatter.parse("2015-01-02")
                var dateParam = Some(date)

                Task.createUserTaskDate("Tarea1", "Dani", dateParam)
                Task.allBeforeDate(dateParam) must have size(0)
            }
        }



        /* TDD TESTS Categories */

        "Devolver el ID al crear una categoria" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                var id = Category.create("Universidad", "Dani")

                id must beGreaterThan(0.toLong)
            }
        }

        "Devolver la lista de categorias" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                Category.create("Universidad", "Dani")

                Category.all() must have size(3)
            }
        }

        "Devolver la lista de categorias de un Usuario" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                val usuario = "Dani"

                Category.create("Asignaturas", usuario)
                Category.create("Nada", "Anonimo")

                Category.all4User(usuario) must have size(3)
            }
        }

        "Devolver el ID al crear una Tarea dentro de una Categoria" in {            
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
                var date = formatter.parse("2015-01-02")
                var dateParam = Some(date)
                var id = Task.createUserTaskDateCategory("Tarea1", "Dani", "Carrera", dateParam)

                id must beGreaterThan(0.toLong)
            }
        }
    }
}