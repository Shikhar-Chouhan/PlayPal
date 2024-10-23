//write all js code here without script tag



//this is for removing the error message from the page
document.addEventListener("DOMContentLoaded", function() {
	// Check if the URL contains the "logout" or "success" parameter
	const url = new URL(window.location);
	let paramsChanged = false;

	// Remove the "logout" parameter if it exists
	if (url.searchParams.has('logout')) {
		url.searchParams.delete('logout');
		paramsChanged = true; // Track if a change was made
	}

	// Remove the "error" parameter if it exists
	if (url.searchParams.has('error')) {
		url.searchParams.delete('error');
		paramsChanged = true; // Track if a change was made
	}


	// Remove the "success" parameter if it exists
	if (url.searchParams.has('success')) {
		url.searchParams.delete('success');
		paramsChanged = true; // Track if a change was made
	}

	// If any parameters were changed, update the URL
	if (paramsChanged) {
		window.history.replaceState({}, document.title, url.toString());
	}
});

//this is for show password
function myFunction() {
	var x = document.getElementById("password");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}

function myFunction1() {
	var x = document.getElementById("password1");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}

