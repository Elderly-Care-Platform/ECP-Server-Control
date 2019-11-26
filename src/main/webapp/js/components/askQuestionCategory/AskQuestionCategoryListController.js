'use strict';

(function (angular) {
    'use strict';

    adminControllers.controller('AskQuestionCategoryListController', AskQuestionCategoryListController)
        .factory('AskQuestionCategoryList', AskQuestionCategoryListFactory);

    AskQuestionCategoryListController.$inject = ['$scope', '$http', '$q', '$location', '$resource', 'DTOptionsBuilder', 'AskQuestionCategoryList'];

    function AskQuestionCategoryListController($scope, $http, $q, $location, $resource, DTOptionsBuilder, AskQuestionCategoryList) {
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


        AskQuestionCategoryList.getAskQuestionCategoryLists().then(function(AskQuestionCategoryLists) {
        	vm.myAskQuestionCategoryLists = AskQuestionCategoryLists;
        });
    }

    AskQuestionCategoryListFactory.$inject = ["$http"];
  
    function AskQuestionCategoryListFactory($http) {
    	return {
    		getAskQuestionCategoryLists: function() {
    			var pathName = location.pathname;
    			return $http.get(pathName + 'api/v1/ask/category/list').then(function(response) {
    				return response.data.data;
    			});
    		}
    	};
    }
}(angular));





