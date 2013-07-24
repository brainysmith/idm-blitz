package services.auth

import java.security.Principal
import javax.security.auth.Subject

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

  def start(ctx: AuthenticationContext): Option[Subject]


}
