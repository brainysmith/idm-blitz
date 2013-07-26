package services.login

/**
 * The Flag value controls the overall behavior as authentication proceeds down the stack.
 * The following represents a description of the valid values for Flag and their respective semantics:
 *    1) Required     - The LoginModule is required to succeed.
 *                      If it succeeds or fails, authentication still continues
 *                      to proceed down the LoginModule list.
 *
 *    2) Requisite    - The LoginModule is required to succeed.
 *                      If it succeeds, authentication continues down the
 *                      LoginModule list.  If it fails,
 *                      control immediately returns to the application
 *                      (authentication does not proceed down the
 *                      LoginModule list).
 *
 *    3) Sufficient   - The LoginModule is not required to
 *                      succeed.  If it does succeed, control immediately
 *                      returns to the application (authentication does not
 *                      proceed down the LoginModule list).
 *                      If it fails, authentication continues down the
 *                      LoginModule list.
 *
 *    4) Optional     - The LoginModule is not required to
 *                      succeed.  If it succeeds or fails,
 *                      authentication still continues to proceed down the
 *                      LoginModule list.
 * The overall authentication succeeds only if all Required and Requisite LoginModules succeed. If a Sufficient
 * LoginModule is configured and succeeds, then only the Required and Requisite LoginModules prior to that Sufficient
 * LoginModule need to have succeeded for the overall authentication to succeed. If no Required or Requisite
 * LoginModules are configured for an application, then at least one Sufficient or Optional LoginModule must succeed.
 */
trait Authenticator {

  //Result`s constants
  val SUCCESS = 0
  val FAIL = 1
  val PRE_AUTH_REQUIRE = 3
  val SUCCESS_WITH_OBLIGATION = 4

  /**
   *
   * @param options
   */
  def init(options: List[(String, String)])


  /**
   * Defines if the authenticator is acceptable to authenticate against the login context.
   * @param lc - context of the current authentication process.
   * @return true if the authenticator is acceptable and false otherwise.
   */
  def isYours(lc: LoginContext): Boolean

  /**
   * Perform the authentication of the current subject which available in the login context.
   * Method authentication and credentials can be accessed by the subject.
   * The result code controls subsequent authentication process (see the result's constants). The following represents
   * a description their respective semantics:
   *    - SUCCESS - if the subject successfully authenticated. The authentication process continues and all principals
   *    will be included in the assertions.
   *    - FAIL - if the subject's credentials are wrong. The authentication process is interrupted. A cause can be added
   *    to the list of the messages.
   *    - PRE_AUTH_REQUIRE - if it requires to do pre-authentication process (e.g: challenge/response protocol).
   *    The client will receive pre_authentication response which contains parameters (e.g: challenge) from the login
   *    context to create the required credentials ans to continue the authentication process.
   *    - SUCCESS_WITH_OBLIGATION - if authentication is successful with the condition to perform the obligation from
   *    the current login context. If obligation perform is successfully then the authentication process continues and
   *    all principals will be included in the assertions.
   * @param lc - context of the current authentication process.
   * @return result code {@see LoginContext}.
   */
  def `do`(lc: LoginContext): Int
}
