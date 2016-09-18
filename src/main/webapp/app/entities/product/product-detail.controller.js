(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ProductDetailController', ProductDetailController);

    ProductDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Product', 'Order'];

    function ProductDetailController($scope, $rootScope, $stateParams, previousState, entity, Product, Order) {
        var vm = this;

        vm.product = entity;
        vm.previousState = previousState.name;
        vm.save = save;
        vm.itemOrder = {quantity:1, product:entity, order:{}};
        
        function save () {
            vm.isSaving = true;
            Order.save(vm.itemOrder, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('salesOrderApp:productUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }
        
        function onSaveError () {
            vm.isSaving = false;
        }

        var unsubscribe = $rootScope.$on('salesOrderApp:productUpdate', function(event, result) {
            vm.product = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
