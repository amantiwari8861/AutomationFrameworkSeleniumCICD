Feature: Login functionality

  Scenario Outline: Login with valid users
    Given the user is on the login page
    When the user logs in with username "<username>" and password "<password>"
    Then the user should be redirected to the inventory page
    And the page title should be "Products"

    Examples:
      | username                | password     |
      | standard_user           | secret_sauce |
      | problem_user            | secret_sauce |
      | performance_glitch_user | secret_sauce |
      | error_user              | secret_sauce |
      | visual_user             | secret_sauce |