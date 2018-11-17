
App Work flow:

Splash screen has animation for 3 ms and after this ad is shown on the screen which can be closed.

I can't be able to login because in response from the link (http://rjtmobile.com/ansari/shopingcart/androidapp/shop_login.php?) I didn't get this msg:"success".

On HomeScreen Top Seller and New Arrival categories are shown.User can choose the items category wise.


SignIn screen has following options:

User can login using mobile no. and password
User can also login using facebook that functionality is currenlty in development phase.
User can signup using username,phonenumber,email id, password

After successful login user can view the product list that is categorized by Best Seller | Top Seller
Item can also be view category wise
Item can be removed or add in the cart
User can pay with paypal or Credit Card
User can view the order history

Does this project follows the best Android development practices?
All product items are extracted from php server page  
In top seller tab all items are stored in array it could be more efficient if it is stored in database table.

Recommendations:
For android app interaction with backend server asynctask with rest api call can be use.

