import Keycloak from "keycloak-js";
const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "n11-realm",
  clientId: "n11-frontend",
});


export default keycloak;
