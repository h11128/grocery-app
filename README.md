# grocery-app

## overview
a grocery app that uses cardView, recyclerview, tablayout, volley, sqlite datebase. 

## issue 
volley can only receive JSONObject with strictly correct string mapping. I used Gson to convert object into jsonstring and convert it into JSONObject. Because Gson automatically escape several punctuation like [" :], I failed to post the object to api and spent a lot of time on it.

volley also can not show the error message when you post to api successfully but that data object has a incorrect format.


