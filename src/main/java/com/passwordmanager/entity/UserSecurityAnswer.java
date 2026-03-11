package com.passwordmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

//======================
//UserSecurityAnswer Entity
//Stores hashed answers for security questions
//======================
@Entity
@Data
@Table(name = "user_security_answers")
public class UserSecurityAnswer {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long answerId; // primary key

 private String answerHash; // hashed answer

 @ManyToOne
 @JoinColumn(name = "user_id", nullable = false)
 @JsonIgnore
 private User user; // associated user

 @ManyToOne(fetch = FetchType.EAGER)
 @JoinColumn(name = "question_id", nullable = false)
 private SecurityQuestion question; // associated question
}