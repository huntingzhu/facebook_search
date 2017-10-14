// Variable defination
var latitude, longitude;
var resultJSON; // The JSON of 5 types;




// Set the customized validation error message;
var inputKeyword = document.getElementById("input-keyword");
inputKeyword.addEventListener("invalid", function() {
  inputKeyword.setCustomValidity("");
  if (inputKeyword.validity.valueMissing) {
      inputKeyword.setCustomValidity("This cant be left empty");
  } else if (inputKeyword.validity.patternMismatch) {
      inputKeyword.setCustomValidity("This cant start with digits and be pure whitespace");
  }
});

inputKeyword.addEventListener("keydown", function() {
    inputKeyword.setCustomValidity("");
});

// Get the current position;
var options = {
  enableHighAccuracy: true,
  timeout: 7000,
  maximumAge: 0
};

function success(pos) {
  var crd = pos.coords;

  latitude = `${crd.latitude}`;
  longitude = `${crd.longitude}`;
  console.log(`Latitude : ${crd.latitude}`);
  console.log(`Longitude: ${crd.longitude}`);
};

function error(err) {
  console.warn(`ERROR(${err.code}): ${err.message}`);
};

navigator.geolocation.getCurrentPosition(success, error, options);

// Hard coding for lat and lng is for speeding up test! Remove it later!!!
// latitude = "34.0334506";
// longitude = "-118.2790048";
// console.log(latitude);
// console.log(longitude);

// When the search form is submitted;
$('#search-form').submit(function(event) {
  event.preventDefault();   // Prevent refresh when submitting the form;
  if(!latitude || !longitude) {
    // window.alert("Cannot get your loaction, So the search result in Places type will not depend on your location!");
  }

  addTableProBar();

  $.ajax({
    url: 'http://sample-env.samqhdps4g.us-west-2.elasticbeanstalk.com/fbsearch/fetchJSON.php',
    // url: 'http://localhost/hw8/fbsearch/fetchJSON.php',
    type: 'GET',
    data: {
      keyword: $('#input-keyword').val().trim(),
      lat: latitude,
      lng: longitude
    },
    success: function(result) {
      console.log("AJAX call is successful!");
      // console.log(result);
      resultJSON = JSON.parse(result);
      rmTableProBar();
      renderTables(resultJSON);
      renderFavor();
      console.log(resultJSON);
    },
    error: function(result) {
      console.log("AJAX call failed!");
      console.log(result);

    }
  });
});


// Find detail information of the user according to user ID;
// @param detail ID;
function createDetail(detailID) {
  addDetailProBar();
  $.ajax({
    url: 'fetchJSON.php',
    type: 'GET',
    data: {
      detailID: detailID
    },
    success: function(result) {
      console.log("Detail AJAX call is successful!");
      // console.log(result);
      var detailJSON = JSON.parse(result);
      console.log(detailJSON);
      rmDetailProBar();
      renderDetail(detailJSON);
    },
    error: function(result) {
      console.log("Detail AJAX call failed!");
      console.log(result);
    }
  });

}

// Render detail table;
// @param: detail JSON object;
function renderDetail(detailJSON) {
  // console.log(detailJSON);

  // For Ablums part;
  if(detailJSON.albums) {
    var albumObj = detailJSON.albums;
    var albumPanel = `<div class="col-xs-12"><div id="albums-panel" class="panel-group"></div></div>`;
    $('#albums').append(albumPanel);
    var albumArr = albumObj.data;
    if(albumArr) {
      var i;
      for(i = 0; i < albumArr.length; i++) {
        var albumContent = `<div class="panel panel-default">
                              <div class="panel-heading" >
                                  <h4 class="panel-title"><a href="/#album`+ i +`" data-toggle="collapse" data-parent="#albums-panel">` + albumArr[i].name + `</a></h4>
                              </div>`;
        if (i == 0) {
          albumContent += `<div class="panel-collapse collapse in" id="album`+ i +`"></div></div>`;
        } else {
          albumContent += `<div class="panel-collapse collapse" id="album`+ i +`"></div></div>`;
        }

        $('#albums-panel').append(albumContent);
        if(albumArr[i].photos && albumArr[i].photos.data) {
          var albumPicArr = albumArr[i].photos.data;
          var pics = `<div class="panel-body">`;
          var j;
          for(j = 0; j < albumPicArr.length; j++) {
            pics += `<img class="album-pic" src="` + albumPicArr[j].picture + `"/>`;
          }
          pics += `</div>`;
          $('#album' + i).append(pics);
        } else {
          var noPicInAlbum = "<div style='text-align: center; padding: 10px;'>There is no photo in this album.<div>";
            $('#album' + i).append(noPicInAlbum);
        }
      }
    } else {
      var noAlbum = `<div class="col-xs-12"><div class="panel panel-warning">
                        <div class="panel-heading" >
                            <h4 class="panel-title">No data found.</h4>
                        </div>
                    </div></div>`;
      $('#albums-panel').append(noAlbum);
    }
  } else {
    var noAlbum = `<div class="col-xs-12"><div class="panel panel-warning">
                      <div class="panel-heading" >
                          <h4 class="panel-title">No data found.</h4>
                      </div>
                  </div></div>`;
    $('#albums').append(noAlbum);
  }

  // For Posts part
  if(detailJSON.posts) {
    var postObj = detailJSON.posts;
    var postPanel = `<div class="col-xs-12"><div id="posts-panel"></div></div>`;
    $('#posts').append(postPanel);
    var postArr = postObj.data;
    if(postArr) {
      var postPic = detailJSON.picture.data.url;
      var postName = detailJSON.name;
      var i;
      for(i = 0; i < postArr.length; i++) {
        if(postArr[i].message && postArr[i].created_time) {
          var postTime = postArr[i].created_time;
          postTime = moment(postTime).format('YYYY-MM-DD hh:mm:ss');

          var postContent = `<div id="post`+ i +`" class="panel panel-default panel-post">
                              <div class ="row">
                                <div class="col-xs-1 pic-div">
                                  <img class="post-pic" src="` + postPic + `" />
                                </div>

                                <div class="col-xs-10 name-div">
                                  <span class="post-name"><b>` + postName + `</b></span></br>
                                  <span class="post-time">` + postTime + `</span>
                                </div>
                              </div>

                              </br>

                              <div class="row">
                                <div class="col-xs-12">` + postArr[i].message + `</div>
                              </div>
                            </div>`;
        }
        $('#posts-panel').append(postContent);

      }
    } else {
      var noPost = `<div class="col-xs-12"><div class="panel panel-warning">
                        <div class="panel-heading" >
                            <h4 class="panel-title">No data found.</h4>
                        </div>
                    </div></div>`;
      $('#posts-panel').append(noPost);
    }
  } else {
    var noPost = `<div class="col-xs-12"><div class="panel panel-warning">
                      <div class="panel-heading" >
                          <h4 class="panel-title">No data found.</h4>
                      </div>
                  </div></div>`;
    $('#posts').append(noPost);
  }


}

// Render tables by using JSON object;
// @param: JSON object;
function renderTables(jsonObj) {
  if(jsonObj) {
    renderUser(jsonObj.user);
    renderGroup(jsonObj.group);
    renderPage(jsonObj.page);
    renderPlace(jsonObj.place);
    renderEvent(jsonObj.event);
  } else {
    console.log("Key word is not submitted yet!");
  }

}

// Render user table;
// @param: user JSON object;
function renderUser(userJSON) {
  // Clear previous table and button;
  $("#user-table-res").remove();
  $("#user-paging").remove();

  // Append table;
  var tableStr = '<div id="user-table-res" class="table-responsive"><table id="user-table" class="table  table-hover"></table></div>';
  $("#user-result").append(tableStr);

  if(userJSON.data && userJSON.data.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#user-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='user-tbody'></tbody>";
    $("#user-table").append(bodyStr);
    var i;
    for(i = 0; i < userJSON.data.length; i++) {
      var picture = userJSON.data[i].picture.data.url;
      var name = userJSON.data[i].name;
      var nameEncoded = escape(name);
      var typeFavor = "user";
      var userID = userJSON.data[i].id;

      // console.log(userID);
      var rowStr = "<tr id='user" + userID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='favorItem(\""+typeFavor+"\",\"" + userID + "\")'><i class='glyphicon glyphicon-star-empty'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+userID+"\")' href='detail/user'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#user-tbody').append(rowStr);
      colorStar("user", userID);
    }
  } else {
    var headStr = `<thead><tr><th>No data found</th></tr></thead>`;
    $("#user-table").append(headStr);
  }

  // Append pagination buttons;
  if(userJSON.paging) { // Test if there needs pagination;
    var pagingDiv = `<div id='user-paging' class='paging'></div>`;
    $('#user-result').append(pagingDiv);

    if(userJSON.paging.previous) { // If there is a previous pagination object;
      var previousURL = userJSON.paging.previous;
      var prevPaging = "<button type=button id='user-prev' class='btn btn-default btn-prev' onclick='callPaging(\"" + previousURL + "\", \"user\")'>Previous</button>";
      $('#user-paging').append(prevPaging);
    }

    if(userJSON.paging.next) { // If there is a next pagination object;
      var nextURL = userJSON.paging.next;
      var nextPaging = "<button type=button id='user-next' class='btn btn-default btn-next' onclick='callPaging(\"" + nextURL + "\", \"user\")'>Next</button>";
      $('#user-paging').append(nextPaging);
    }
  }
}

// Render page table;
// @param: page JSON object;
function renderPage(pageJSON) {
  // Clear previous table and button;
  $("#page-table-res").remove();
  $("#page-paging").remove();

  // Append table;
  var tableStr = '<div id="page-table-res" class="table-responsive"><table id="page-table" class="table table-hover "></table></div>';
  $("#page-result").append(tableStr);

  if(pageJSON.data && pageJSON.data.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#page-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='page-tbody'></tbody>";
    $("#page-table").append(bodyStr);
    var i;
    for(i = 0; i < pageJSON.data.length; i++) {
      var picture = pageJSON.data[i].picture.data.url;
      var name = pageJSON.data[i].name;
      var nameEncoded = escape(name);
      var typeFavor = "page";
      var pageID = pageJSON.data[i].id;

      // console.log(pageID);
      var rowStr = "<tr id='page" + pageID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='favorItem(\""+typeFavor+"\",\"" + pageID + "\")'><i class='glyphicon glyphicon-star-empty'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+pageID+"\")' href='detail/page'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#page-tbody').append(rowStr);
      colorStar("page", pageID);
    }
  } else {
    var headStr = `<thead><tr><th>No data found</th></tr></thead>`;
    $("#page-table").append(headStr);
  }

  // Append pagination buttons;
  if(pageJSON.paging) { // Test if there needs pagination;
    var pagingDiv = `<div id='page-paging' class='paging'></div>`;
    $('#page-result').append(pagingDiv);

    if(pageJSON.paging.previous) { // If there is a previous pagination object;
      var previousURL = pageJSON.paging.previous;
      var prevPaging = "<button type=button id='page-prev' class='btn btn-default btn-prev' onclick='callPaging(\"" + previousURL + "\", \"page\")'>Previous</button>";
      $('#page-paging').append(prevPaging);
    }

    if(pageJSON.paging.next) { // If there is a next pagination object;
      var nextURL = pageJSON.paging.next;
      var nextPaging = "<button type=button id='page-next' class='btn btn-default btn-next' onclick='callPaging(\"" + nextURL + "\", \"page\")'>Next</button>";
      $('#page-paging').append(nextPaging);
    }
  }
}

// Render event table;
// @param: event JSON object;
function renderEvent(eventJSON) {
  // Clear previous table and button;
  $("#event-table-res").remove();
  $("#event-paging").remove();

  // Append table;
  var tableStr = '<div id="event-table-res" class="table-responsive"><table id="event-table" class="table table-hover "></table></div>';
  $("#event-result").append(tableStr);

  if(eventJSON.data && eventJSON.data.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#event-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='event-tbody'></tbody>";
    $("#event-table").append(bodyStr);
    var i;
    for(i = 0; i < eventJSON.data.length; i++) {
      var picture = eventJSON.data[i].picture.data.url;
      var name = eventJSON.data[i].name;
      var nameEncoded = escape(name);
      var typeFavor = "event";
      var eventID = eventJSON.data[i].id;

      // console.log(eventID);
      var rowStr = "<tr id='event" + eventID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='favorItem(\""+typeFavor+"\",\"" + eventID + "\")'><i class='glyphicon glyphicon-star-empty'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+eventID+"\")' href='detail/event'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#event-tbody').append(rowStr);
      colorStar("event", eventID);
    }
  } else {
    var headStr = `<thead><tr><th>No data found</th></tr></thead>`;
    $("#event-table").append(headStr);
  }

  // Append pagination buttons;
  if(eventJSON.paging) { // Test if there needs pagination;
    var pagingDiv = `<div id='event-paging' class='paging'></div>`;
    $('#event-result').append(pagingDiv);

    if(eventJSON.paging.previous) { // If there is a previous pagination object;
      var previousURL = eventJSON.paging.previous;
      var prevPaging = "<button type=button id='event-prev' class='btn btn-default btn-prev' onclick='callPaging(\"" + previousURL + "\", \"event\")'>Previous</button>";
      $('#event-paging').append(prevPaging);
    }

    if(eventJSON.paging.next && eventJSON.data.length == 25) { // If there is a next pagination object;
      var nextURL = eventJSON.paging.next;
      // console.log(nextURL);
      var nextPaging = "<button type=button id='event-next' class='btn btn-default btn-next' onclick='callPaging(\"" + nextURL + "\", \"event\")'>Next</button>";
      $('#event-paging').append(nextPaging);
    }
  }
}

// Render place table;
// @param: place JSON object;
function renderPlace(placeJSON) {
  // Clear previous table and button;
  $("#place-table-res").remove();
  $("#place-paging").remove();

  // Append table;
  var tableStr = '<div id="place-table-res" class="table-responsive"><table id="place-table" class="table table-hover "></table></div>';
  $("#place-result").append(tableStr);

  if(placeJSON.data && placeJSON.data.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#place-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='place-tbody'></tbody>";
    $("#place-table").append(bodyStr);
    var i;
    for(i = 0; i < placeJSON.data.length; i++) {
      var picture = placeJSON.data[i].picture.data.url;
      var name = placeJSON.data[i].name;
      var nameEncoded = escape(name);
      var typeFavor = "place";
      var placeID = placeJSON.data[i].id;

      // console.log(placeID);
      var rowStr = "<tr id='place" + placeID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='favorItem(\""+typeFavor+"\",\"" + placeID + "\")'><i class='glyphicon glyphicon-star-empty'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+placeID+"\")' href='detail/place'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#place-tbody').append(rowStr);
      colorStar("place", placeID);
    }
  } else {
    var headStr = `<thead><tr><th>No data found</th></tr></thead>`;
    $("#place-table").append(headStr);
  }

  // Append pagination buttons;
  if(placeJSON.paging) { // Test if there needs pagination;
    var pagingDiv = `<div id='place-paging' class='paging'></div>`;
    $('#place-result').append(pagingDiv);

    if(placeJSON.paging.previous) { // If there is a previous pagination object;
      var previousURL = placeJSON.paging.previous;
      var prevPaging = "<button type=button id='place-prev' class='btn btn-default btn-prev' onclick='callPaging(\"" + previousURL + "\", \"place\")'>Previous</button>";
      $('#place-paging').append(prevPaging);
    }

    if(placeJSON.paging.next) { // If there is a next pagination object;
      var nextURL = placeJSON.paging.next;
      // console.log(nextURL);
      var nextPaging = "<button type=button id='place-next' class='btn btn-default btn-next' onclick='callPaging(\"" + nextURL + "\", \"place\")'>Next</button>";
      $('#place-paging').append(nextPaging);
    }
  }
}

// Render group table;
// @param: group JSON object;
function renderGroup(groupJSON) {
  // Clear previous table and button;
  $("#group-table-res").remove();
  $("#group-paging").remove();

  // Append table;
  var tableStr = '<div id="group-table-res" class="table-responsive"><table id="group-table" class="table table-hover "></table></div>';
  $("#group-result").append(tableStr);

  if(groupJSON.data && groupJSON.data.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#group-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='group-tbody'></tbody>";
    $("#group-table").append(bodyStr);
    var i;
    for(i = 0; i < groupJSON.data.length; i++) {
      var picture = groupJSON.data[i].picture.data.url;
      var name = groupJSON.data[i].name;
      var nameEncoded = escape(name);
      var typeFavor = "group";
      var groupID = groupJSON.data[i].id;

      // console.log(groupID);
      var rowStr = "<tr id='group" + groupID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='favorItem(\""+typeFavor+"\",\"" + groupID + "\")'><i class='glyphicon glyphicon-star-empty'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+groupID+"\")' href='detail/group'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#group-tbody').append(rowStr);
      colorStar("group", groupID);
    }
  } else {
    var headStr = `<thead><tr><th>No data found</th></tr></thead>`;
    $("#group-table").append(headStr);
  }

  // Append pagination buttons;
  if(groupJSON.paging) { // Test if there needs pagination;
    var pagingDiv = `<div id='group-paging' class='paging'></div>`;
    $('#group-result').append(pagingDiv);

    if(groupJSON.paging.previous) { // If there is a previous pagination object;
      var previousURL = groupJSON.paging.previous;
      var prevPaging = "<button type=button id='group-prev' class='btn btn-default btn-prev' onclick='callPaging(\"" + previousURL + "\", \"group\")'>Previous</button>";
      $('#group-paging').append(prevPaging);
    }

    if(groupJSON.paging.next && groupJSON.data.length == 25) { // If there is a next pagination object;
      var nextURL = groupJSON.paging.next;
      // console.log(nextURL);
      var nextPaging = "<button type=button id='group-next' class='btn btn-default btn-next' onclick='callPaging(\"" + nextURL + "\", \"group\")'>Next</button>";
      $('#group-paging').append(nextPaging);
    }
  }
}

// Render favorite table;
function renderFavor() {
  // Clear previous table and button;
  $("#favor-table-res").remove();

  // Append table;
  var tableStr = '<div id="favor-table-res" class="table-responsive"><table id="favor-table" class="table table-hover "></table></div>';
  $("#favor-result").append(tableStr);

  if(localStorage.length != 0) {
    // Append table header;
    var headStr = `<thead><tr><th>#</th><th>Profile photo</th><th>Name</th><th>Type</th><th>Favorite</th><th>Details</th></tr></thead>`;
    $("#favor-table").append(headStr);

    // Append table rows;
    var bodyStr = "<tbody id='favor-tbody'></tbody>";
    $("#favor-table").append(bodyStr);
    var i;
    for(i = 0; i < localStorage.length; i++) {
      // console.log(groupJSON.data[i].picture.data.url);
      var favorJSON = JSON.parse(localStorage.getItem(localStorage.key(i)));
      // console.log(favorJSON);
      var picture = favorJSON.picture;
      var name = favorJSON.name;
      var nameEncoded = escape(name);
      var typeFavor = favorJSON.type;
      var detailID = favorJSON.detailID;
      var rowStr = "<tr id='favor" + detailID + "'><td>" + (i+1) + "</td>";
      rowStr += "<td><img class='profile-pic' src='" + picture + "'/></td>";
      rowStr += "<td>" + escapeHtml(name) + "</td>";
      rowStr += "<td>" + typeFavor + "</td>";
      rowStr += "<td><button class='btn btn-default btn-favor' onclick='rmFavor(\"" + typeFavor + "\",\"" + detailID + "\")'><i class='glyphicon glyphicon-trash'></i></button></td>";
      rowStr += "<td><a class='btn btn-default btn-detail' onclick='sendFavorData(\""+picture+"\",\""+nameEncoded+"\",\""+typeFavor+"\",\""+detailID+"\")' href='detail/favor'><i class='glyphicon glyphicon-chevron-right'></i></a></td></tr>";
      $('#favor-tbody').append(rowStr);
    }
  } else {
    var headStr = `<thead><tr><th>No data has been stored</th></tr></thead>`;
    $("#favor-table").append(headStr);
  }
}

// Remove favorite item;
function rmFavor(type, detailID) {
  var favorID = type + detailID;
  var favorRow = document.getElementById(favorID);
  $('#' + type + detailID + ' td .btn-favor i').removeClass('glyphicon-star');
  $('#' + type + detailID + ' td .btn-favor i').addClass('glyphicon-star-empty');
  localStorage.removeItem(favorID);
  renderFavor();
}


// Save and unsave favoritei tem, store it in the local storage;
// @param type and detailID;
function favorItem(type, detailID) {

  var favorID = type + detailID;
  var favorRow = document.getElementById(favorID);
  // console.log(favorID);

  if(checkFavor(type, detailID)) {
    $('#' + type + detailID + ' td .btn-favor i').removeClass('glyphicon-star');
    $('#' + type + detailID + ' td .btn-favor i').addClass('glyphicon-star-empty');
    localStorage.removeItem(favorID);
  } else {
    $('#' + type + detailID + ' td .btn-favor i').removeClass('glyphicon-star-empty');
    $('#' + type + detailID + ' td .btn-favor i').addClass('glyphicon-star');

    // console.log(favorRow);
    var favorJSON = {
                      "picture" : favorRow.childNodes[1].childNodes[0].attributes.src.nodeValue,
                      "name" : favorRow.childNodes[2].childNodes[0].nodeValue,
                      "type" : type,
                      "detailID" : detailID
                    }
    // console.log(favorJSON);
    localStorage.setItem(favorID, JSON.stringify(favorJSON));
  }
  renderFavor();
  // console.log(localStorage);



}

// Painting color for the star icon according to local storage;
// @param type and detailID;
function colorStar(type, detailID) {
  if(checkFavor(type, detailID)) {
    $('#' + type + detailID + ' td .btn-favor i').removeClass('glyphicon-star-empty');
    $('#' + type + detailID + ' td .btn-favor i').addClass('glyphicon-star');
  } else {
    $('#' + type + detailID + ' td .btn-favor i').removeClass('glyphicon-star');
    $('#' + type + detailID + ' td .btn-favor i').addClass('glyphicon-star-empty');
  }
}

// Check whether this item is in local storage;
// @param type and detailID;
// @return true if it exists, otherwise return false;
function checkFavor(type, detailID) {
  var item = localStorage.getItem(type + detailID);
  if(item) {
    return true;
  } else {
    return false;
  }
}

// Reverse animatation effect for leaving detail-view;
function reverseAnimate() {
  $('#view-container').removeClass('reverse');
}

// Reverse animatation effect for entering into detail-view;
function reverseAnimateBack() {
  $('#view-container').addClass('reverse');
}

// Call AJAX to fetch the prev or next page;
// @param: type param could be user, page, event, place or group;
function callPaging(pageURL, type) {
  // console.log(pageURL);
  $.ajax({
    url: pageURL,
    type: 'GET',
    success: function(result) {
      // console.log("Paging AJAX call is successful!");
      // console.log(result);
      switch(type) {
        case "user":
          resultJSON.user = result;
          renderUser(result);
          break;
        case "page":
          resultJSON.page = result;
          renderPage(result);
          break;
        case "event":
          resultJSON.event = result;
          renderEvent(result);
          break;
        case "place":
          resultJSON.place = result;
          renderPlace(result);
          break;
        case "group":
          resultJSON.group = result;
          renderGroup(result);
          break;
        default:
          console.log("Unknown type: " + type);
          break;
      }
    },
    error: function(result) {
      console.log("Paging AJAX call failed!");
      console.log(result);

    }
  });
}

// Add progress bar;
function addTableProBar() {
  var proBar =`<div class="progress">
                  <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100" style="width:50%">
                  </div>
                </div>`;
  $('#user-result').empty();
  $('#page-result').empty();
  $('#event-result').empty();
  $('#place-result').empty();
  $('#group-result').empty();
  $('#favor-result').empty();

  $('#table-progressbar').append(proBar);
}

// Remove progress bar;
function rmTableProBar() {
  $('#table-progressbar').empty();
}

// Add progress bar;
function addDetailProBar() {
  var proBar =`<div class="col-xs-12"><div class="progress">
                  <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100" style="width:50%">
                  </div>
                </div></div>`;

  $('#albums').append(proBar);
  $('#posts').append(proBar);
}

// Remove progress bar;
function rmDetailProBar() {
  $('#albums').empty();
  $('#posts').empty();
}

// Escape html special characters;
function escapeHtml(text) {
  return text
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
}
