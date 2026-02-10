const search = () => {
  let query = $("#search-input").val();

  if (query === "") {
    $(".search-result").hide();
  } else {
    console.log(query);

    let url = `http://localhost:8080/search/${query}`;

    fetch(url)
      .then(response => response.json())   // ✅ CALL THE FUNCTION
      .then(data => {
        //console.log(data);                 // ✅ Actual data will print
        
        let text = `<div class='list-group'>`

        data.forEach((contact)=>{
          text+=`<a href='/user/${contact.cId}/contact' class="list-group-item list-group-item-action">${contact.name}</a>`
        })


        text+=`</div>`

        $(".search-result").html(text);
        $(".search-result").show();



      })
      .catch(error => {
        console.error("Error:", error);
      });

    $(".search-result").show();
  }
};

