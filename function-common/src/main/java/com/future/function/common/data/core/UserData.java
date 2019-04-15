package com.future.function.common.data.core;

/**
 * Interface for accessing data of object of {@code User} class type.
 * <p>
 * Useful in validation layer for validation purposes of level {@link
 * java.lang.annotation.ElementType#TYPE}.
 */
public interface UserData {
  
  /**
   * Method to get role of a user object, which is actually an enum, but
   * returned as String.
   *
   * @return {@code String} -
   * {@link com.future.function.common.enumeration.core.Role} enum converted
   * to String object.
   */
  String getRoleAsString();
  
  /**
   * Method to get batch of a user object, which is actually another
   * interface, available only for user with role STUDENT.
   *
   * @return {@code Long} - Either batch number or null, depending on
   * whether batch exists in user object or not.
   */
  Long getBatchNumber();
  
  /**
   * Method to get university name of a user object, available only for user
   * with role STUDENT.
   *
   * @return {@code String} - String value of university.
   */
  String getUniversity();
  
}
