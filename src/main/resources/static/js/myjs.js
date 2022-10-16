console.log("this is scripted file")

const toggleSidebar = () =>{
   
     if($('.sidebar').is(".visible")){
       
         $(".sidebar").css("display","none")
         $("content").css("margin-left","0%")

     }
       else{
        $(".sidebar").css("display","block")
        $("content").css("margin-left","20%")

       }
     
};

const search=()=>{
 // console.log("searching");

 let query=$("#search-input").val();
 

  if(query==''){
    $(".searchresult").hide();
  }
  else{
    console.log(query)
    
    //sending to backend
   
     let url=`http://localhost:9090/search/${query}`;

     fetch(url).then((response) =>{
      return response.json();
     })
     .then((data) => {

      //console.log(data);

     let text=`<div class='list-group'>`;
      
      data.forEach((contact) => {
        text+=`<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'> ${contact.name}</a>`
      });

          
      text+=`</div>`;

      $(".searchresult").html(text);
      $(".searchresult").show();
     });

    
  }
}

//first request to server to create order

const paymentStart=()=>{
  console.log("payment started")
  var amount=$("#payment").val();
  console.log(amount);

  if(amount==""|| amount==null){
    alert("amount is required");
    return;
  }
}
