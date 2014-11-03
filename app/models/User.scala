package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

object User {

   def exists(login: String): Boolean = DB.withConnection { implicit c =>
      SQL("select count(*) from usertask where login = {login}").on(
          'login -> login).as(scalar[Long].single) == 1
   }

   def getUser(login: String): Option[String] = DB.withConnection { implicit c =>

      SQL("select login from usertask where login = {login}").on(
         'login -> login
      ).as(scalar[String].singleOpt)
   }
}