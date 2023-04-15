function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}

function sendMsg(phoneNumber){
    return $axios({
        'url': '/user/send?phoneNumber='+phoneNumber,
        'method': 'get',
    })
}
