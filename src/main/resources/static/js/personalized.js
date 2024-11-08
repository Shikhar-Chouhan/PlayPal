function getRecommendations() {
    const ageGroup = document.getElementById('age').value;
    const activityLevel = document.getElementById('activity-level').value;
    const preference = document.getElementById('indoor-outdoor').value;

    let recommendations = '';
    
    if (ageGroup === 'kids') {
        recommendations += '<h3>Recommended for Kids:</h3>';
        recommendations += '<p>Cricket, Badminton, Swimming, Football</p>';
    } else if (ageGroup === 'teens') {
        recommendations += '<h3>Recommended for Teens:</h3>';
        recommendations += '<p>Basketball, Tennis, Track and Field, Cricket</p>';
    } else {
        recommendations += '<h3>Recommended for Adults:</h3>';
        recommendations += '<p>Yoga, Running, Cycling, Football, Tennis</p>';
    }

    if (activityLevel === 'high') {
        recommendations += '<h4>For High Activity Level:</h4>';
        recommendations += '<p>Football, Basketball, CrossFit</p>';
    } else if (activityLevel === 'moderate') {
        recommendations += '<h4>For Moderate Activity Level:</h4>';
        recommendations += '<p>Badminton, Tennis, Swimming</p>';
    } else {
        recommendations += '<h4>For Low Activity Level:</h4>';
        recommendations += '<p>Yoga, Walking, Golf</p>';
    }

    if (preference === 'indoor') {
        recommendations += '<h4>Indoor Sports:</h4>';
        recommendations += '<p>Badminton, Table Tennis, Squash</p>';
    } else if (preference === 'outdoor') {
        recommendations += '<h4>Outdoor Sports:</h4>';
        recommendations += '<p>Cricket, Football, Cycling</p>';
    } else {
        recommendations += '<h4>Indoor & Outdoor Sports:</h4>';
        recommendations += '<p>Tennis, Swimming, Basketball</p>';
    }

    document.getElementById('recommendations').innerHTML = recommendations;
    return false;  // Prevent form submission
}
