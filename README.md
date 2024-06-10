<h1>Зміни в допоміжному проєкті</h1>
В проєкт із Сутністю 1 <i><b>Person</b></i>
<p>
<i>https://github.com/streamlined2/REST-application</i>
<p>добавлена підтримка Kafka producer. У разі зміни або видалення сутності 1 застосунок надсилає повідомлення у топік <i><b>notification</b></i> із допомогою сервіса нотифікації<p>
https://github.com/streamlined2/REST-application/blob/main/src/main/java/com/streamlined/restapp/service/notification/DefaultNotificationService.java<p>
і бінів KafkaAdmin, KafkaTemplate
<p>https://github.com/streamlined2/REST-application/blob/main/src/main/java/com/streamlined/restapp/service/notification/kafka/KafkaSender.java<p>

<h1>Параметри підключення до Email сервера</h1>
зберігаються в файлі <i><b>.env</b></i>, який зчитується <i><b>application.yml</b></i>.<p>
Пароль для підключення до SMTP серверу створений відповідно до інструкції<p>
https://support.google.com/mail/answer/185833?hl=en
<p>
і задається як VM параметр застосунку<p>
-Dspring.mail.password=secret<p>

<h1>Імідж застосунку</h1>
Опис іміджу наведений в файлі <i><b>Dockerfile</b></i>, побудова виконується командою<p>
docker build --tag emailsender:1.0 .
