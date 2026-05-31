# 🌍 GHADS

## Gaza Humanitarian Aid Distribution System

> A humanitarian aid management system designed to improve transparency, fairness, and efficiency in aid distribution across Gaza.

---

## 📖 Project Overview

The Gaza Humanitarian Aid Distribution System (GHADS) is a desktop application developed using JavaFX and MySQL to support humanitarian organizations in managing aid distribution processes.

The system was designed to address one of the most common challenges in humanitarian work: duplicate aid distribution and the lack of centralized beneficiary records.

By providing a unified platform for organizations, coordinators, and beneficiary families, GHADS helps ensure that humanitarian assistance reaches those who need it most while maintaining accurate and transparent records.

💡 The core idea behind the project is not only storing data, but also supporting decision-making and improving fairness in aid allocation.

---

## 🎯 Project Objectives

GHADS aims to:

✅ Register and manage beneficiary families.

✅ Manage humanitarian organizations and coordinators.

✅ Record and monitor aid distribution operations.

✅ Prevent duplicate aid distribution within a 30-day period.

✅ Prioritize highly vulnerable families.

✅ Maintain accurate historical records.

✅ Improve transparency and accountability.

---

## 📁 Project Structure

```text
src
│
├── app
│   └── Main.java
│
├── controller
│   ├── LoginController.java
│   ├── AdminDashboardController.java
│   └── CoordinatorDashboardController.java
│
├── dao
│   ├── UserDAO.java
│   ├── OrganizationDAO.java
│   ├── FamilyDAO.java
│   └── AidDistributionDAO.java
│
├── model
│   ├── User.java
│   ├── Organization.java
│   ├── Family.java
│   └── AidDistribution.java
│
├── config
│   └── DBConnection.java
│
├── view
│   ├── Login.fxml
│   ├── AdminDashboard.fxml
│   └── CoordinatorDashboard.fxml
│
├── css
│   ├── login-style.css
│   └── admin-style.css
│
└── images
    └── exit.png
```

---

## 🏢 Participating Organizations

| Organization                             | Abbreviation |
| ---------------------------------------- | ------------ |
| World Food Programme                     | WFP          |
| Palestine Red Crescent Society           | PRCS         |
| International Committee of the Red Cross | ICRC         |
| Egyptian Red Crescent                    | ERC          |
| United Nations Relief and Works Agency   | UNRWA        |

تمثل هذه المؤسسات الجهات المسؤولة عن تقديم المساعدات الإنسانية للعائلات المسجلة داخل النظام.

---

## 👥 User Roles

### 🔐 Administrator

The Administrator has full access to the system and can:

* Manage organizations.
* Manage coordinators.
* Manage families.
* View aid distribution records.
* Search aid records.
* Monitor dashboard statistics.
* Change passwords.

يمتلك المدير صلاحيات كاملة لإدارة جميع بيانات النظام ومتابعة عمليات توزيع المساعدات.

---

### 👨‍💼 Coordinator

Each coordinator belongs to a specific organization and can:

* Register families.
* View family records.
* Record aid distributions.
* Update profile information.
* Change password.

يقوم المنسق بإدخال بيانات العائلات وتسجيل عمليات توزيع المساعدات التابعة لمؤسسته.

---

## 🏠 Family Management

The system stores important information about beneficiary families:

* Household Name
* Phone Number
* Location
* Family Size
* National ID
* Vulnerability Level
* Registration Date
* Last Aid Date

يساعد ذلك في تحديد مستوى الاحتياج لكل عائلة ومتابعة المساعدات التي حصلت عليها سابقاً.

---

## 📦 Aid Distribution Management

The system supports multiple aid categories:

🍞 Food Assistance

💵 Cash Assistance

🏥 Medical Assistance

👕 Clothing Assistance

Every aid distribution record contains:

* Family Name
* Organization
* Coordinator
* Aid Type
* Distribution Date

يسمح ذلك بتوثيق جميع عمليات التوزيع والرجوع إليها في أي وقت.

---

## ⚠️ Business Rule (Core Feature)

One of the most important features implemented in GHADS is preventing duplicate aid distribution.

Before recording a new aid distribution, the system checks previous distributions during the last 30 days.

### Vulnerability-Based Decision

🟢 HIGH Vulnerability

* Distribution is allowed.

🟡 MEDIUM Vulnerability

* Distribution is rejected if aid was already received within the previous 30 days.

🔴 LOW Vulnerability

* Distribution is rejected if aid was already received within the previous 30 days.

تعتمد فكرة المشروع الأساسية على إعطاء الأولوية للعائلات ذات الاحتياج المرتفع ومنع تكرار المساعدات للعائلات ذات الاحتياج المتوسط أو المنخفض خلال فترة ثلاثين يوماً.

---

## 📊 Dashboard & Analytics

The system provides dashboards that display:

📌 Total Organizations

📌 Total Coordinators

📌 Total Families

📌 Served Families

📌 Unserved Families

تساعد هذه الإحصائيات على إعطاء صورة سريعة عن حالة النظام وحجم المساعدات المقدمة.

---

## 🛠️ Technologies Used

### Development

* Java
* JavaFX
* Scene Builder

### Database

* MySQL
* JDBC

### Design

* CSS

### Software Engineering Concepts

* MVC Architecture
* DAO Pattern
* Singleton Pattern

تم استخدام هذه المفاهيم لضمان تنظيم الكود وسهولة صيانته وتطويره مستقبلاً.

---

## 🗄️ Database Structure

The database consists of four main entities:

### Organization

Stores organization information.

### User

Stores system users and permissions.

### Family

Stores beneficiary family information.

### AidDistribution

Stores all aid distribution transactions.

تم تصميم قاعدة البيانات بطريقة تعكس سير العمل الحقيقي داخل المؤسسات الإنسانية.

---

## ✨ Additional Features

✔ Modern JavaFX User Interface

✔ Dashboard Statistics

✔ Organization Management (CRUD)

✔ User Management (CRUD)

✔ Family Management (CRUD)

✔ Aid Distribution Tracking

✔ Organization Filtering

✔ Aid Type Tracking

✔ Password Validation

✔ Change Password

✔ About Application Window

✔ Theme Customization

✔ Font Customization

✔ Exit Menu with Icon

---

## 🚀 Learning Outcomes

Through this project, practical experience was gained in:

* JavaFX Application Development
* Database Design
* JDBC Integration
* MVC Architecture
* DAO Pattern Implementation
* Data Validation
* Human-Centered Software Design

ساعد المشروع على ربط المفاهيم النظرية بالتطبيق العملي من خلال بناء نظام متكامل يخدم مشكلة واقعية.

---

## 👩‍💻 Developed By

**Manar Abu Arab** 


### 🎓 Teaching Assistant

**Aya Alharazin**

---

## ❤️ Final Note

GHADS is more than a database application.

It represents a practical attempt to use technology in support of humanitarian work by improving organization, reducing duplication, and helping aid reach the families that need it most.

يمثل المشروع نموذجاً عملياً لكيفية توظيف البرمجيات في خدمة العمل الإنساني وتحسين كفاءة إدارة المساعدات وتوزيعها بشكل عادل ومنظم.
