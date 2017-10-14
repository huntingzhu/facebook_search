// Facebook SDK;
window.fbAsyncInit = function() {
  FB.init({
    appId      : '1170207316430580',
    xfbml      : true,
    version    : 'v2.8'
  });
  FB.AppEvents.logPageView();
};

(function(d, s, id){
   var js, fjs = d.getElementsByTagName(s)[0];
   if (d.getElementById(id)) {return;}
   js = d.createElement(s); js.id = id;
   js.src = "//connect.facebook.net/en_US/sdk.js";
   fjs.parentNode.insertBefore(js, fjs);
 }(document, 'script', 'facebook-jssdk'));

// MODULE
var myNgApp = angular.module('myNgApp', ['ngRoute', 'ngAnimate']);

myNgApp.config(function ($routeProvider, $locationProvider) {

  $routeProvider

  .when('/', {
    templateUrl: 'pages/table.html',
    controller: 'tableController'
  })

  .when('/detail/:typeActive', {
    templateUrl: 'pages/detail.html',
    controller: 'detailController'
  });

  $locationProvider.html5Mode(true);

});

// SERVICE
myNgApp.service('sharedProperties', function () {
        var typeActive = 'user';

        return {
            getTypeActive: function () {
                return typeActive;
            },
            setTypeActive: function(value) {
                typeActive = value;
            }
        };
});

// CONTROLLERS
myNgApp.controller('indexController', ['$scope', '$location', 'sharedProperties', function($scope, $location, sharedProperties) {
  $scope.go = function (path) {
    $location.path(path);
  };

  // When the clear button is clicked;
  $scope.clearTable = function() {
    sharedProperties.setTypeActive('user');
    $('#user-result').empty();
    $('#page-result').empty();
    $('#event-result').empty();
    $('#place-result').empty();
    $('#group-result').empty();

    $('#input-keyword').val('');

    switchActive("user");
    $("#user").addClass("active");
    $("#page").removeClass("active");
    $("#event").removeClass("active");
    $("#place").removeClass("active");
    $("#group").removeClass("active");
    $("#favor").removeClass("active");

    resultJSON = null ; // Empty result JSON;

    $location.path("/");
  }

}]);

// CONTROLLERS
myNgApp.controller('tableController', ['$scope', 'sharedProperties', function($scope, sharedProperties) {
  $scope.$on('$viewContentLoaded', function() {
    renderTables(resultJSON);
    renderFavor();
    var typeActive = sharedProperties.getTypeActive();
    switchActive(typeActive);
    reverseAnimateBack();

  });
}]);

// CONTROLLERS
myNgApp.controller('detailController', ['$scope', '$routeParams', '$location', 'sharedProperties', function($scope, $routeParams, $location, sharedProperties) {
  var typeActive= $routeParams.typeActive;
  sharedProperties.setTypeActive(typeActive);

  $scope.picture = middleData.getPicture();
  $scope.name = unescape(middleData.getName());
  $scope.typeFavor = middleData.getTypeFavor();
  $scope.detailID = middleData.getDetailID();

  $scope.go = function(path) {
    $location.path(path);
  };


  $scope.detailFavor = function(typeFavor, detailID) {
    var favorID = typeFavor + detailID;
    if(checkFavor(typeFavor, detailID)) {
      $('#detail-favor-btn i').removeClass('glyphicon-star');
      $('#detail-favor-btn i').addClass('glyphicon-star-empty');
      localStorage.removeItem(favorID);
    } else {
      $('#detail-favor-btn i').removeClass('glyphicon-star-empty');
      $('#detail-favor-btn i').addClass('glyphicon-star');

      var favorJSON = {
                      "picture" : $scope.picture,
                      "name" : $scope.name,
                      "type" : $scope.typeFavor,
                      "detailID" : $scope.detailID
                    }
      localStorage.setItem(favorID, JSON.stringify(favorJSON));
    }
    renderFavor();
  }

  // Facebook posts
  $scope.fbPost = function() {
    console.log("fb-post fires!");
    FB.ui({
      app_id: '1170207316430580',
      method: 'feed',
      display: 'popup',
      link: "http://sample-env.samqhdps4g.us-west-2.elasticbeanstalk.com/fbsearch/", picture: $scope.picture, name: $scope.name, caption: "FB SEARCH FROM USC CSCI571",
      }, function(response){
      if (response && !response.error_message) {
        window.alert("Posted Successfully");
      } else {
        window.alert("Not Posted");
      }
    });
  }

  $scope.$on('$viewContentLoaded', function() {
    reverseAnimate();

    createDetail($scope.detailID);
    if(checkFavor($scope.typeFavor, $scope.detailID)) {
      $('#detail-favor-btn i').removeClass('glyphicon-star-empty');
      $('#detail-favor-btn i').addClass('glyphicon-star');
    } else {
      $('#detail-favor-btn i').removeClass('glyphicon-star');
      $('#detail-favor-btn i').addClass('glyphicon-star-empty');
    }

  });

}]);


// Remove and add active class;
function switchActive(type) {
  switch(type) {
    case "user":
      rmAddActive(['#user-result'],['#page-result','#event-result','#place-result','#group-result','#favor-result']);
      break;
    case "page":
      rmAddActive(['#page-result'],['#user-result','#event-result','#place-result','#group-result','#favor-result']);
      break;
    case "event":
      rmAddActive(['#event-result'],['#page-result','#user-result','#place-result','#group-result','#favor-result']);
      break;
    case "place":
      rmAddActive(['#place-result'],['#page-result','#event-result','#user-result','#group-result','#favor-result']);
      break;
    case "group":
      rmAddActive(['#group-result'],['#page-result','#event-result','#place-result','#user-result','#favor-result']);
      break;
    case "favor":
      rmAddActive(['#favor-result'],['#page-result','#event-result','#place-result','#group-result','#user-result']);
      break;
    default:
      console.log("Unknown type: " + type);
      break;
  }
}

// Add and remove class "active";
function rmAddActive(addArr, rmArr) {
  var i;
  for(i = 0; i < addArr.length; i++) {
    $(addArr[i]).addClass('active');
    $(addArr[i]).addClass('in');
  }

  for(i = 0; i < rmArr.length; i++) {
    $(rmArr[i]).removeClass('active');
    $(rmArr[i]).removeClass('in');
  }
}

// This is used to pass data from table view to detail view;
function sendFavorData(picture, name, typeFavor, detailID) {

  middleData.setPicture(picture);
  middleData.setName(name);
  middleData.setTypeFavor(typeFavor);
  middleData.setDetailID(detailID);

}

// This is used to store middle data from table view to detail view;
var middleData = (function() {
  var picture = "";
  var name = "";
  var typeFavor = "";
  var detailID = "";

  return {
    getPicture: function () {
        return picture;
    },
    setPicture: function(value) {
        picture = value;
    },

    getName: function () {
        return name;
    },
    setName: function(value) {
        name = value;
    },

    getTypeFavor: function () {
        return typeFavor;
    },
    setTypeFavor: function(value) {
        typeFavor = value;
    },

    getDetailID: function () {
        return detailID;
    },
    setDetailID: function(value) {
        detailID = value;
    }
  }
})();
