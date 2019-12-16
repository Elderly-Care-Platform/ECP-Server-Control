'use strict';

(function (angular) {
    'use strict';

    adminControllers
            .controller('AskQuestionListController', AskQuestionListController)
            .factory('AskQuestionList', AskQuestionListFactory);
    
    AskQuestionListController.$inject = ['$scope', '$http', '$q', '$location', '$resource', 'DTOptionsBuilder', 'AskQuestionList'];

    function AskQuestionListController($scope, $http, $q, $location, $resource, DTOptionsBuilder, AskQuestionList) {
        var vm = this;     
        
        if (localStorage.getItem("ADMIN_USER_ROLE") !== 'SUPER_USER' && localStorage.getItem("ADMIN_USER_ROLE") !== 'EDITOR') {
    		$location.path('/users/login');
    		return;
    	}
        
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withPaginationType('full_numbers')
			.withDisplayLength(100)
			.withOption("bAutoWidth", false)
			.withOption('searchable', false)
        
        $scope.host = location.host;
        $scope.pathName = location.pathname;
        
        
        AskQuestionList.getAskQuestionLists().then(function(AskQuestionLists) {
        	vm.myAskQuestionLists = AskQuestionLists;
        });
    }
    
    AskQuestionListFactory.$inject = ["$http"];
  
    function AskQuestionListFactory($http) {
    	return {
    		getAskQuestionLists: function() {	
    			var pathName = location.pathname;
    			return $http.get(pathName + 'api/v1/ask/list').then(function(response) {
    				return response.data.data;
    			});
    		}
    	};
    }

  
}(angular));





