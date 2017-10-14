<?php
// Require Facebook SDK;
require_once __DIR__ . '/php-graph-sdk-5.0.0/src/Facebook/autoload.php';

header('Access-Control-Allow-Origin:*');
header('Access-Control-Allow-Methods:GET');

// Define variables;
$keyword = "";
$latitude = "";
$longitude = "";
$detailID;     // ID number, used for querying a detailed table;
$accessToken = "EAAQoTB9ozvQBAKThmyBkSATXZABCJVOr5nG8iEICkubBLYwQ95Abbr0dbEBTWuAdapLLHOMyPIxtnpCu8NVdurCgwhpXmCptOkMgfxLNEEqQUa8EXzKSPOGZAvRGUCCxJmJkjA1LhdCecFUxSUg7RdERAFXbBFK33rZBWRvuQZDZD";
// $APIkey = "AIzaSyBnljONyah4HHYm6Zx7XexcMAW0dvYQLCI";

// Set facebook sdk object
$fb = new Facebook\Facebook([
  'app_id' => '1170207316430580',
  'app_secret' => 'a883429ac304c17acac7f1b0241be786',
  'default_graph_version' => 'v2.8',
]);

// Set access token;
$fb->setDefaultAccessToken($accessToken);

// Trim data and encode input data;
function trimData($data) {
  $data = trim($data);
  $data = urlencode($data);
  $data = stripslashes($data);
  $data = htmlspecialchars($data);
  return $data;
}

// Get JSON for types of user, group, page and event;
function get_JSON($type) {
  // Declare global variables;
  global $fb, $keyword;
  // Use Facebook SDK to code;
  // Returns a `Facebook\FacebookResponse` object
  $request = $fb->request('GET', '/search', ['limit' => '10', 'q' => $keyword, 'type' => $type, 'fields'=>'id,name,picture.width(700).height(700)']);
  $response = $fb->getClient()->sendRequest($request);
  $arrayJSON = $response->getDecodedBody();
  $jsonObj = json_encode($arrayJSON);

  return $jsonObj;
  // return var_dump($response);
}

// Get JSON for type of place;
function get_place_JSON() {
  // Declare global variables;
  global $fb, $keyword, $latitude, $longitude;
  // Use Facebook SDK to code;
  // Returns a `Facebook\FacebookResponse` object
  if(empty($latitude) || empty($longitude)) {
    $request = $fb->request('GET', '/search', ['limit' => '10', 'q' => $keyword,'type' => 'place', 'fields'=>'id,name,picture.width(700).height(700)']);
    $response = $fb->getClient()->sendRequest($request);
    $arrayJSON = $response->getDecodedBody();
    $jsonObj = json_encode($arrayJSON);
  } else {
    $request = $fb->request('GET', '/search', ['limit' => '10', 'q' => $keyword,'type' => 'place','center' => $latitude . "," . $longitude, 'fields'=>'id,name,picture.width(700).height(700)']);
    $response = $fb->getClient()->sendRequest($request);
    $arrayJSON = $response->getDecodedBody();
    $jsonObj = json_encode($arrayJSON);
  }


  return $jsonObj;
}

// Get JSON for detail info;
function get_detail_JSON() {
  // Declare global variables;
  global $fb, $detailID;

  try {
    $request = $fb->request('GET', '/'. $detailID, ['fields'=>'id,name,picture.width(700).height(700),albums.limit(5){name,photos.limit(2){name,picture}},posts.limit(5)']);
    // echo $request->getUrl();
    $response = $fb->getClient()->sendRequest($request);
    $detailArrayJSON = $response->getDecodedBody();

    // Get high resolution pictures;
    if(isset($detailArrayJSON["albums"])) {
      if(isset($detailArrayJSON["albums"]["data"])) {
        foreach ($detailArrayJSON["albums"]["data"] as &$album) {
          if(isset($album['photos']['data'])) {
            foreach ($album['photos']['data'] as &$pic) {
              // query for high resol pictures;
              $pictureID = $pic['id'];
              // echo $pictureID;
              $urlIDField = "/" . $pictureID . "/picture";
              $request = $fb->request('GET', $urlIDField, []);
              $response = $fb->getClient()->sendRequest($request);
              $picHeader= $response->getHeaders();
              $picURL = $picHeader['Location'];

              // Replace original picture URL
              $pic['picture'] = $picURL;
            }
          }
        }
      }
    }

    $jsonObj = json_encode($detailArrayJSON);

  } catch(Facebook\Exceptions\FacebookResponseException $e) {
    $jsonObj = '{}';
  } catch(Facebook\Exceptions\FacebookSDKException $e) {
    $jsonObj = '{}';
  }

  return $jsonObj;
}


// Check if the form is posted
if($_SERVER["REQUEST_METHOD"] == "GET") {
  if(empty($_GET['detailID'])) { // If it is not called for detail JSON;
    if(!empty($_GET["keyword"])) {
      $keyword = trimData($_GET["keyword"]);
      $latitude = $_GET["lat"];
      $longitude = $_GET["lng"];

      // Get all the JSON;
      $userJSON = get_JSON('user');
      $groupJSON = get_JSON('group');
      $pageJSON = get_JSON('page');
      $placeJSON = get_place_JSON();
      $eventJSON = get_JSON('event');

      $allJSON = "{\"user\": " . $userJSON
                . ", \"group\": " . $groupJSON
                . ", \"page\": " . $pageJSON
                . ", \"place\": " . $placeJSON
                . ", \"event\": " . $eventJSON . "}";

      echo $allJSON;
    }
  } else { // It is called for detail JSON;
    $detailID = $_GET['detailID'];
    $detailJSON = get_detail_JSON();



    echo $detailJSON;
  }
} else {
  echo "{}";
}






 ?>
