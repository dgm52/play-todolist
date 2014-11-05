package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current


case class Category(id: Long, name: String, user: String)   

object Category {

   val category = {
      get[Long]("id") ~
      get[String]("name") ~
      get[String]("user") map {
         case id~name~user => Category(id, name, user)
      }
   }

   def create(name: String, user: String): Long = DB.withConnection { implicit c =>
      
      val id: Option[Long] = SQL("insert into category (user, name) values ({login}, {name})").on(
         'login -> user,
         'name -> name
      ).executeInsert()

      val b: Long = id match {  
          case Some(id) => id
          case None => 0  
      }      

      id.getOrElse(-1)
   }

   def all(): List[Category] = DB.withConnection { implicit c =>
      SQL("select * from category").as(category *)
   }
}