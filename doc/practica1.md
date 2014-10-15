# Aplicación play-todolist

*Práctica 1 de la asignatura MADS - Grado en Ingeniería Informática Universidad de Alicante*
* * *

## Descripción general
Aplicación web de gestión de listas de tareas por hacer. Desarrollada en el lenguaje de programación Scala bajo el framework **Play Framework**, continuación de la aplicación ejemplo del tutorial de Play Framework. 

Permite crear tareas bajo un nombre de usuario o bajo un pseudónimo Anónimo. De forma opcional permite introducir una fecha límite para cada tarea. 

Puedes listar las tareas según un usuario, y según una fecha exacta o una fecha máxima.

## Arquitectura de la aplicación

En esta primera versión se ha creado una aplicación sobre un **API REST** trabajando, evidentemente, con el formato JSON en las respuestas HTTP. Como usuario usarás las rutas para realizar las operaciones, conformadas con esta estructura:

####Home
> 
- `GET /`      Home Page

####Feature 1: API REST básica
> 
- `GET     /tasks`                         Devuelve todas las tareas del usuario Anónimo
> 
>
- `GET     /tasks/:id`                     Obtiene la tarea indicada. Ej: `GET /tasks/20`
>
>
- `POST    /tasks`                         Crea una tarea bajo el usuario Anónimo
>
>
- `DELETE  /tasks/:id`                     Elimina la tarea indicada. Ej: `DELETE /tasks/20`

####Feature 2: Usuario creador de la tarea
> - `GET     /:login/tasks`                  Devuelve todas las tareas del usuario indicado. Ej: `GET /Dani/tasks`
>
>
> - `POST    /:login/tasks`                  Crea una tarea bajo el usuario indicado. Ej: `POST /Dani/tasks`

####Feature 3: Fecha en la tarea
> - `GET     /:dateBefore/before/tasks`      Devuelve todas las tareas cuya fecha límite sean anteriores a la fecha indicada. Ej: `GET /2015-01-01/before/tasks`
>
> - `GET     /:login/:enddate/tasks`         Devuelve todas las tareas del usuario indicado cuya fecha límite coincida exactamente con la fecha indicada. Ej: `GET /Dani/2014-10-06/tasks`
>
> - `POST    /:login/:enddate/tasks`         Crea una tarea bajo el usuario indicado junto una fecha límite. Ej: `POST /Dani/2014-10-01/tasks`

En esta *Feature 3* se han desarrollado las funciones de **Tareas en una fecha límite exacta** y **Tareas con fecha límite antes de la fecha seleccionada**.

En las operaciones POST, evidentemente, se requiere asignar mediante parámetros las etiquetas o nombres de las tareas. Enviaremos las peticiones REST a través del plugin **Postman**. 

Las peticiones devolverán una respuesta HTTP de tipo 200 OK cuando las operaciones solicitadas contengan (en el caso de la petición) un usuario registrado. Si el usuario no existe, la petición devolverá un **Error 404 NOT FOUND**.

Las rutas de las peticiones son el canal de acceso a los recursos disponibles. Serán accedidos mediante los controladores concretos que recogen y validan los parámetros donde sean necesarios. Una vez recogidos se comunican con la capa de Domain Model de nuestra aplicación.

### Desarrollo de la aplicación
Se ha implementado bajo la función del control de versiones Git. Los incrementos desarrollados han sido volcados sobre un repositorio privado **Bitbucket**.

Para ello, se han desarrollado los incrementos en base a las nuevas feautres añadidas en ramas de proyectos, siendo mezcladas con la rama master una vez finalizadas y totalmente completadas para continuar con el desarrollo principal de la aplicación.

Como consiguiente, se ha volcado y subido el proyecto de la primera práctica y versión sobre el repositorio Github creado con este propósito. Github nos tiene preparadas una serie de características de estadística muy interesantes.



* * *

*Práctica de la asignatura MADS - Grado en Ingeniería Informática Universidad de Alicante*
>>>>>>> b189df62e3642d486eda4eda1560bf396305f41b
