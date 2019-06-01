package com.future.function.web.model.request.core;

import com.future.function.common.data.core.UserData;
import com.future.function.validation.annotation.core.EmailMustBeUnique;
import com.future.function.validation.annotation.core.FileMustExist;
import com.future.function.validation.annotation.core.Name;
import com.future.function.validation.annotation.core.OnlyStudentCanHaveBatchAndUniversity;
import com.future.function.validation.annotation.core.Phone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Model representation for user web request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@OnlyStudentCanHaveBatchAndUniversity
public class UserWebRequest implements UserData {
  
  @NotNull(message = "NotNull")
  private String role;
  
  @EmailMustBeUnique
  @Email(message = "Email")
  @NotBlank(message = "NotBlank")
  private String email;
  
  @Name
  @NotBlank(message = "NotBlank")
  private String name;
  
  @Phone
  private String phone;
  
  @NotBlank(message = "NotBlank")
  private String address;
  
  private String batch;
  
  private String university;
  
  @FileMustExist
  private List<String> avatar;
  
}
